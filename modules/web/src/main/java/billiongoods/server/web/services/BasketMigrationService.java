package billiongoods.server.web.services;

import billiongoods.core.Member;
import billiongoods.core.Personality;
import billiongoods.core.Visitor;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.basket.BasketManager;
import billiongoods.server.services.state.PersonalityStateListener;
import billiongoods.server.services.state.PersonalityStateManager;
import billiongoods.server.web.security.web.filter.visitor.VisitorServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BasketMigrationService {
	private BasketManager basketManager;
	private PersonalityStateManager stateManager;
	private PlatformTransactionManager transactionManager;

	private final PersonalityStateListener stateListener = new ThePersonalityStateListener();

	private static final DefaultTransactionAttribute NEW_TRANSACTION_DEFINITION = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

	private static final Logger log = LoggerFactory.getLogger("billiongoods.server.web.services.BasketMigrationService");

	public BasketMigrationService() {
	}

	private void migratePersonalityState(Personality personality) {
		if (!(personality instanceof Member)) {
			return;
		}

		final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

		final Cookie cookie = WebUtils.getCookie(request, VisitorServices.VISITOR_KEY);
		if (cookie != null) {
			try {
				final Visitor visitor = new Visitor(Long.decode(cookie.getValue()));

				final Basket visitorBasket = basketManager.getBasket(visitor);
				if (visitorBasket != null) {
					final TransactionStatus transaction = transactionManager.getTransaction(NEW_TRANSACTION_DEFINITION);
					try {
						for (BasketItem item : visitorBasket.getBasketItems()) {
							basketManager.addBasketItem(personality, item.getProduct(), item.getOptions(), item.getQuantity());
						}
						basketManager.closeBasket(visitor);
						transactionManager.commit(transaction);
					} catch (Exception ex) {
						transactionManager.rollback(transaction);
						throw ex;
					}
				}
			} catch (Exception ex) {
				log.error("Basket can't be migrated for person: " + personality, ex);
			}
		}
	}

	public void setBasketManager(BasketManager basketManager) {
		this.basketManager = basketManager;
	}

	public void setStateManager(PersonalityStateManager stateManager) {
		if (this.stateManager != null) {
			this.stateManager.removePlayerStateListener(stateListener);
		}

		this.stateManager = stateManager;

		if (this.stateManager != null) {
			this.stateManager.addPersonalityStateListener(stateListener);
		}
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	private final class ThePersonalityStateListener implements PersonalityStateListener {
		private ThePersonalityStateListener() {
		}

		@Override
		public void playerOnline(Personality person) {
			migratePersonalityState(person);
		}

		@Override
		public void playerAlive(Personality person) {
		}

		@Override
		public void playerOffline(Personality person) {
		}
	}
}
