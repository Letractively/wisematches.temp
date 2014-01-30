package billiongoods.server.services.notify.impl.center;


import billiongoods.core.Passport;
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
import billiongoods.server.services.validator.ValidationChange;
import billiongoods.server.services.validator.ValidationListener;
import billiongoods.server.services.validator.ValidationManager;
import billiongoods.server.services.validator.ValidationSummary;
import billiongoods.server.warehouse.ProductManager;
import billiongoods.server.warehouse.ProductPreview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private void processOrderAccepted(Order order) {
		try {
			Recipient owner = null;

			Long personId = order.getPersonId();
			if (personId != null) {
				final Account account = accountManager.getAccount(personId);
				if (account != null) {
					owner = Recipient.get(account);
				}
			}

			if (owner == null) {
				owner = Recipient.get(order.getPayer(), new Passport(order.getPayerName()));
			}
			final Recipient recipient = Recipient.get(Recipient.MailBox.MONITORING, owner);

			notificationService.raiseNotification(recipient, Sender.SERVER, "system.order", order, order.getId());
		} catch (Exception ex) {
			log.error("Order accepted alert can't be sent", ex);
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
			this.productValidator.removeValidationListener(validatorListener);
		}

		this.productValidator = productValidator;

		if (this.productValidator != null) {
			this.productValidator.addValidationListener(validatorListener);
		}
	}

	private class TheOrderListener implements OrderListener {

		private TheOrderListener() {
		}

		@Override
		public void orderStateChanged(Order order, OrderState oldState, OrderState newState) {
			if (newState == OrderState.ACCEPTED) {
				processOrderAccepted(order);
			}
		}

	}

	private class TheAccountListener implements AccountListener {
		private TheAccountListener() {
		}

		@Override
		public void accountCreated(Account account) {
			raiseAlarm("system.account", account, account.getPassport().getUsername());
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
		public void validationFinished(ValidationSummary summary) {
			raiseAlarm("system.validation", summary, summary.getStartDate());
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
			final ProductPreview preview = productManager.getPreview(tracking.getProductId());
			raiseAlarm("system." + tracking.getTrackingType().name().toLowerCase(), preview, preview.getId() + ": " + preview.getName());
		}

		@Override
		public void trackingRemoved(ProductTracking tracking) {
		}

		@Override
		public void trackingInvalidated(ProductTracking tracking) {
		}
	}
}
