package billiongoods.server.services.notify.impl.center;


import billiongoods.core.account.Account;
import billiongoods.core.account.AccountManager;
import billiongoods.core.task.BreakingDayListener;
import billiongoods.server.services.notify.NotificationException;
import billiongoods.server.services.notify.NotificationService;
import billiongoods.server.services.notify.Recipient;
import billiongoods.server.services.notify.Sender;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderListener;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.payment.OrderState;
import billiongoods.server.services.tracking.ProductTracking;
import billiongoods.server.services.tracking.ProductTrackingManager;
import billiongoods.server.services.tracking.TrackingContext;
import billiongoods.server.services.tracking.TrackingType;
import billiongoods.server.warehouse.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class NotificationOriginCenter implements BreakingDayListener {
	private OrderManager orderManager;
	private ProductManager productManager;
	private AccountManager accountManager;
	private ProductTrackingManager trackingManager;

	private NotificationService notificationService;

	private final OrderListener orderListener = new TheOrderListener();
	private final ProductStateListener productStateListener = new TheStateProductListener();

	private static final EnumSet<OrderState> ORDER_STATES =
			EnumSet.of(OrderState.ACCEPTED, OrderState.PROCESSING, OrderState.SHIPPED, OrderState.SUSPENDED, OrderState.CANCELLED);

	private static final Logger log = LoggerFactory.getLogger("billiongoods.notification.OriginCenter");

	public NotificationOriginCenter() {
	}

	private void processOrderState(Order order) {
		Account account;
		Recipient recipient = null;
		if (order.getPersonId() != null && (account = accountManager.getAccount(order.getPersonId())) != null) {
			recipient = Recipient.get(account);
		} else if (order.isTracking() && order.getPayer() != null) {
			recipient = Recipient.get(order.getPayer());
		}

		if (recipient != null) {
			fireNotification("order.state", recipient, order, order.getId());
		}
	}

	private void processTrackingNotifications(ProductPreview preview, TrackingType type) {
		final TrackingContext c = new TrackingContext(preview.getId(), type);
		final List<ProductTracking> tracks = trackingManager.searchEntities(c, null, null, null);
		for (ProductTracking tracking : tracks) {
			Account account;
			Recipient recipient;
			if (tracking.getPersonId() != null && (account = accountManager.getAccount(tracking.getPersonId())) != null) {
				recipient = Recipient.get(account);
			} else {
				recipient = Recipient.get(tracking.getPersonEmail());
			}

			if (recipient != null) {
				fireNotification("product." + type.name().toLowerCase(), recipient, preview, preview.getId());
				trackingManager.removeTracking(tracking.getId());
			}
		}
	}

	private void fireNotification(String code, Recipient recipient, Object context, Object... args) {
		try {
			notificationService.raiseNotification(recipient, Sender.UNDEFINED, code, context, args);
			log.info("Notification was raised to {} [{}]", recipient, code);
		} catch (NotificationException ex) {
			log.error("Notification can't be sent to player: code=" + code + ", recipient=" + recipient, ex);
		}
	}

	@Override
	public void breakingDayTime(Date midnight) {
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
		this.accountManager = accountManager;
	}

	public void setProductManager(ProductManager productManager) {
		if (this.productManager != null) {
			this.productManager.removeProductStateListener(productStateListener);
		}

		this.productManager = productManager;

		if (this.productManager != null) {
			this.productManager.addProductStateListener(productStateListener);
		}
	}

	public void setTrackingManager(ProductTrackingManager trackingManager) {
		this.trackingManager = trackingManager;
	}

	public void setNotificationService(NotificationService notificationDistributor) {
		this.notificationService = notificationDistributor;
	}


	private class TheOrderListener implements OrderListener {
		private TheOrderListener() {
		}

		@Override
		public void orderStateChanged(Order order, OrderState oldState, OrderState newState) {
			if (ORDER_STATES.contains(newState)) {
				processOrderState(order);
			} else {
				log.info("Order's new state doesn't suppose notifications: {} -> {}", order.getId(), newState);
			}
		}
	}

	private class TheStateProductListener implements ProductStateListener {
		private TheStateProductListener() {
		}

		@Override
		public void productPriceChanged(ProductPreview preview, Price oldPrice, Price newPrice) {
		}

		@Override
		public void productStockChanged(ProductPreview preview, StockInfo oldStock, StockInfo newStock) {
			if (newStock.getStockState() == StockState.IN_STOCK) {
				processTrackingNotifications(preview, TrackingType.AVAILABILITY);
			}
		}

		@Override
		public void productStateChanged(ProductPreview preview, ProductState oldState, ProductState newState) {
			if (newState == ProductState.ACTIVE && oldState != ProductState.ACTIVE) {
				processTrackingNotifications(preview, TrackingType.DESCRIPTION);
			}
		}
	}
}
