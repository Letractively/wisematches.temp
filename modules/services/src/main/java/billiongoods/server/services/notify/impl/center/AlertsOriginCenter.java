package billiongoods.server.services.notify.impl.center;


import billiongoods.core.account.Account;
import billiongoods.core.account.AccountListener;
import billiongoods.core.account.AccountManager;
import billiongoods.server.services.notify.NotificationService;
import billiongoods.server.services.notify.Recipient;
import billiongoods.server.services.notify.Sender;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderListener;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.payment.OrderState;
import billiongoods.server.services.price.PriceRenewal;
import billiongoods.server.services.price.PriceValidator;
import billiongoods.server.services.price.PriceValidatorListener;
import billiongoods.server.services.price.ValidationSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AlertsOriginCenter {
	private OrderManager orderManager;
	private AccountManager accountManager;
	private PriceValidator priceValidator;

	private NotificationService notificationService;

	private final TheOrderListener orderListener = new TheOrderListener();
	private final TheAccountListener accountListener = new TheAccountListener();
	private final ThePriceValidatorListener validatorListener = new ThePriceValidatorListener();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.alerts.OriginCenter");

	public AlertsOriginCenter() {
	}

	protected void raiseAlarm(String subj, Object context) {
		try {
			notificationService.raiseNotification(subj, Recipient.ALERTS_BOX, Sender.SUPPORT, context);
		} catch (Exception ex) {
			log.error("Alerts can't be sent: subj=[{}], msg=[{}]", subj, context);
		}
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}


	public void setOrderManager(OrderManager orderManager) {
		if (this.orderManager != null) {
			this.orderManager.removeOrderListener(orderListener);
		}

		this.orderManager = orderManager;

		if (this.orderManager != null) {
			this.orderManager.addOrderListener(orderListener);
		}
	}

	public void setAccountManager(AccountManager accountManager) {
		if (this.accountManager != null) {
			this.accountManager.removeAccountListener(accountListener);
		}

		this.accountManager = accountManager;

		if (this.accountManager != null) {
			this.accountManager.addAccountListener(accountListener);
		}
	}

	public void setPriceValidator(PriceValidator priceValidator) {
		if (this.priceValidator != null) {
			this.priceValidator.removePriceValidatorListener(validatorListener);
		}

		this.priceValidator = priceValidator;

		if (this.priceValidator != null) {
			this.priceValidator.addPriceValidatorListener(validatorListener);
		}
	}

	private class TheOrderListener implements OrderListener {
		private TheOrderListener() {
		}

		@Override
		public void orderStateChange(Order order, OrderState oldState, OrderState newState) {
			if (newState == OrderState.ACCEPTED) {
				raiseAlarm("system.order", order);
			}
		}
	}

	private class TheAccountListener implements AccountListener {
		private TheAccountListener() {
		}

		@Override
		public void accountCreated(Account account) {
			raiseAlarm("system.account", account);
		}

		@Override
		public void accountRemove(Account account) {
		}

		@Override
		public void accountUpdated(Account oldAccount, Account newAccount) {
		}
	}

	private class ThePriceValidatorListener implements PriceValidatorListener {
		private ThePriceValidatorListener() {
		}

		@Override
		public void priceValidationStarted(Date date, int articlesCount) {
		}

		@Override
		public void priceValidated(Integer articleId, PriceRenewal renewal) {
		}

		@Override
		public void priceValidationFinished(Date date, ValidationSummary summary) {
			raiseAlarm("system.price", summary);
		}
	}
}
