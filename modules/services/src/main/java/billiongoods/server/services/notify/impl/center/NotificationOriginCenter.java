package billiongoods.server.services.notify.impl.center;


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
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class NotificationOriginCenter implements BreakingDayListener {
	private OrderManager orderManager;
	private ProductManager productManager;
	private ProductTrackingManager trackingManager;

	private NotificationService notificationService;

	private final OrderListener orderListener = new TheOrderListener();
	private final ProductStateListener productStateListener = new TheStateProductListener();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.notification.OriginCenter");

	public NotificationOriginCenter() {
	}

	private void processOrderState(Order order) {
		if (order.isTracking() && order.getPayer() != null) {
			fireNotification("order.state", new Recipient(order.getPayer()), order);
		}
	}

	private void processProductStock(ProductDescription description, StockInfo oldStock, StockInfo newStock) {
		if (newStock.getStockState() == StockState.IN_STOCK) {
			final TrackingContext c = new TrackingContext(description.getId(), TrackingType.AVAILABILITY);

			final List<ProductTracking> tracks = trackingManager.searchEntities(c, null, null, null);
			for (ProductTracking tracking : tracks) {
				fireNotification("product.availability", new Recipient(tracking.getPersonEmail()), description);
				trackingManager.removeTracking(tracking.getId());
			}
		}
	}

	private void processProductState(ProductDescription description, ProductState oldState, ProductState newState) {
		if (newState == ProductState.ACTIVE) {
			final TrackingContext c = new TrackingContext(description.getId(), TrackingType.DESCRIPTION);

			final List<ProductTracking> tracks = trackingManager.searchEntities(c, null, null, null);
			for (ProductTracking tracking : tracks) {
				fireNotification("product.description", new Recipient(tracking.getPersonEmail()), description);
				trackingManager.removeTracking(tracking.getId());
			}
		}
	}

	private void fireNotification(String code, Recipient recipient, Object context) {
		try {
			notificationService.raiseNotification(code, recipient, Sender.UNDEFINED, context);
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
		public void orderStateChange(Order order, OrderState oldState, OrderState newState) {
			processOrderState(order);
		}
	}

	private class TheStateProductListener implements ProductStateListener {
		private TheStateProductListener() {
		}

		@Override
		public void productPriceChanged(ProductDescription description, Price oldPrice, Price newPrice) {
		}

		@Override
		public void productStockChanged(ProductDescription description, StockInfo oldStock, StockInfo newStock) {
			processProductStock(description, oldStock, newStock);
		}

		@Override
		public void productStateChanged(ProductDescription description, ProductState oldState, ProductState newState) {
			processProductState(description, oldState, newState);
		}
	}
}
