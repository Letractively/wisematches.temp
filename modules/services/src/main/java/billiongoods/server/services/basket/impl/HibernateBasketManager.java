package billiongoods.server.services.basket.impl;

import billiongoods.core.Personality;
import billiongoods.core.Visitor;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.basket.BasketManager;
import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.ArticleManager;
import billiongoods.server.warehouse.AttributeManager;
import billiongoods.server.warehouse.Property;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateBasketManager implements BasketManager {
	private SessionFactory sessionFactory;

	private ArticleManager articleManager;
	private AttributeManager attributeManager;

	public HibernateBasketManager() {
	}

	@Override
	@Cacheable("basket")
	public HibernateBasket getBasket(Personality principal) {
		if (principal == null) {
			return null;
		}
		return getBasketOrCreate(principal, false);
	}

	@Override
	@Cacheable("basketSize")
	public int getBasketSize(Personality principal) {
		if (principal == null) {
			return 0;
		}
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("select count(elements(b.basketItems)) from billiongoods.server.services.basket.impl.HibernateBasket as b where b.principal=:pid");
		query.setParameter("pid", principal.getId());
		return ((Number) query.uniqueResult()).intValue();
	}

	private HibernateBasket getBasketOrCreate(Personality principal, boolean create) {
		final Session session = sessionFactory.getCurrentSession();

		HibernateBasket basket = (HibernateBasket) session.get(HibernateBasket.class, principal.getId());
		if (basket == null && create) {
			Integer expirationDays = null;
			if (principal instanceof Visitor) {
				expirationDays = 7;
			}
			basket = new HibernateBasket(principal, expirationDays);

			session.save(basket);
		}

		if (basket != null) {
			basket.initialize(articleManager, attributeManager);
		}
		return basket;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Basket closeBasket(Personality principal) {
		if (principal != null) {
			final Session session = sessionFactory.getCurrentSession();

			final Basket basket = (Basket) session.get(HibernateBasket.class, principal.getId());
			if (basket != null) {
				session.delete(basket);
			}
			return basket;
		}
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	@CacheEvict(value = {"basket", "basketSize"}, key = "#principal")
	public BasketItem addBasketItem(Personality principal, ArticleDescription article, List<Property> options, int quantity) {
		final HibernateBasketItem item = new HibernateBasketItem(article, options, quantity);

		final HibernateBasket basket = getBasketOrCreate(principal, true);
		basket.addBasketItem(item);

		sessionFactory.getCurrentSession().update(basket);

		return item;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	@CacheEvict(value = {"basket", "basketSize"}, key = "#principal")
	public BasketItem removeBasketItem(Personality principal, int itemNumber) {
		final HibernateBasket basket = getBasket(principal);

		if (basket != null) {
			final HibernateBasketItem basketItem = basket.getBasketItem(itemNumber);
			if (basketItem != null) {
				basket.removeBasketItem(basketItem);
				sessionFactory.getCurrentSession().update(basket);
			}
			return basketItem;
		}
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	@CacheEvict(value = {"basket", "basketSize"}, key = "#principal")
	public BasketItem updateBasketItem(Personality principal, int itemNumber, int quantity) {
		final HibernateBasket basket = getBasket(principal);
		if (basket != null) {
			final HibernateBasketItem basketItem = basket.getBasketItem(itemNumber);
			if (basketItem != null) {
				basketItem.setQuantity(quantity);
				sessionFactory.getCurrentSession().update(basketItem);
			}
			return basketItem;
		}
		return null;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setArticleManager(ArticleManager articleManager) {
		this.articleManager = articleManager;
	}

	public void setAttributeManager(AttributeManager attributeManager) {
		this.attributeManager = attributeManager;
	}
}
