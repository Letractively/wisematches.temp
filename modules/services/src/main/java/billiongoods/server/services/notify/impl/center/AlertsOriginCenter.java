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
import billiongoods.server.services.tracking.ProductTracking;
import billiongoods.server.services.tracking.ProductTrackingListener;
import billiongoods.server.services.tracking.ProductTrackingManager;
import billiongoods.server.services.validator.*;
import billiongoods.server.warehouse.ProductManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AlertsOriginCenter {
	private OrderManager orderManager;
	private AccountManager accountManager;
	private ProductManager productManager;
	private ValidationManager productValidator;
	private ProductTrackingManager trackingManager;

	private NotificationService notificationService;

	private final TheOrderListener orderListener = new TheOrderListener();
	private final TheAccountListener accountListener = new TheAccountListener();
	private final TheValidationListener validatorListener = new TheValidationListener();
	private final TheProductTrackingListener trackingListener = new TheProductTrackingListener();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.alerts.OriginCenter");

	public AlertsOriginCenter() {
	}

	protected void raiseAlarm(String code, Object context, Object... args) {
		try {
			notificationService.raiseNotification(Recipient.get(Recipient.MailBox.MONITORING), Sender.SERVER, code, context, args);
		} catch (Exception ex) {
			log.error("Alerts can't be sent: code=[{}], msg=[{}]", code, context);
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

	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	public void setTrackingManager(ProductTrackingManager trackingManager) {
		if (this.trackingManager != null) {
			this.trackingManager.removeProductTrackingListener(trackingListener);
		}

		this.trackingManager = trackingManager;

		if (this.trackingManager != null) {
			this.trackingManager.addProductTrackingListener(trackingListener);
		}
	}

	public void setValidationManager(ValidationManager productValidator) {
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
		public void orderStateChanged(Order order, OrderState oldState, OrderState newState) {
			if (newState == OrderState.ACCEPTED) {
				raiseAlarm("system.order", order, order.getId());
			}
		}
	}

	private class TheAccountListener implements AccountListener {
		private TheAccountListener() {
		}

		@Override
		public void accountCreated(Account account) {
			raiseAlarm("system.account", account, account.getUsername());
		}

		@Override
		public void accountRemove(Account account) {
		}

		@Override
		public void accountUpdated(Account oldAccount, Account newAccount) {
		}
	}

	private class TheValidationListener implements ValidationListener {
		private TheValidationListener() {
		}

		@Override
		public void validationStarted(ValidationSummary summary) {
		}

		@Override
		public void validationFinished(ValidationSummary summary, List<ValidatingProduct> broken) {
			final Map<String, Object> context = new HashMap<>(2);
			context.put("broken", broken);
			context.put("summary", summary);
			raiseAlarm("system.validation", context, summary.getStartDate());
		}

		@Override
		public void validationProcessed(ValidationChange validation) {
		}
	}

	private class TheProductTrackingListener implements ProductTrackingListener {
		private TheProductTrackingListener() {
		}

		@Override
		public void trackingAdded(ProductTracking tracking) {
			raiseAlarm("system." + tracking.getTrackingType().name().toLowerCase(), productManager.getPreview(tracking.getProductId()));
		}

		@Override
		public void trackingRemoved(ProductTracking tracking) {
		}
	}
}
