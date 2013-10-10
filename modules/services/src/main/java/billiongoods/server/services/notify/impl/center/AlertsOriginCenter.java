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
import billiongoods.server.services.validator.ProductValidation;
import billiongoods.server.services.validator.ProductValidationManager;
import billiongoods.server.services.validator.ValidationProgressListener;
import billiongoods.server.services.validator.ValidationSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AlertsOriginCenter {
	private OrderManager orderManager;
	private AccountManager accountManager;
	private ProductValidationManager productValidator;

	private NotificationService notificationService;

	private final TheOrderListener orderListener = new TheOrderListener();
	private final TheAccountListener accountListener = new TheAccountListener();
	private final TheValidationProgressListener validatorListener = new TheValidationProgressListener();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.alerts.OriginCenter");

	public AlertsOriginCenter() {
	}

	protected void raiseAlarm(String subj, Object context) {
		try {
			notificationService.raiseNotification(subj, Recipient.MONITORING, Sender.SERVER, context);
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

	public void setValidationManager(ProductValidationManager productValidator) {
		if (this.productValidator != null) {
			this.productValidator.removeValidationProgressListener(validatorListener);
		}

		this.productValidator = productValidator;

		if (this.productValidator != null) {
			this.productValidator.addValidationProgressListener(validatorListener);
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

	private class TheValidationProgressListener implements ValidationProgressListener {
		private TheValidationProgressListener() {
		}

		@Override
		public void validationStarted(Date date, int totalCount) {
		}

		@Override
		public void productValidated(Integer productId, ProductValidation validation) {
		}

		@Override
		public void validationFinished(Date date, ValidationSummary summary) {
			raiseAlarm("system.validation", summary);
		}
	}
}
