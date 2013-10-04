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
import billiongoods.server.warehouse.Product;
import billiongoods.server.warehouse.ProductListener;
import billiongoods.server.warehouse.ProductManager;
import billiongoods.server.warehouse.ProductState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class NotificationOriginCenter implements BreakingDayListener, InitializingBean {
	private OrderManager orderManager;
	private ProductManager productManager;
	private ProductTrackingManager trackingManager;

	private NotificationService notificationService;

	private final OrderListener orderListener = new TheOrderListener();
	private final ProductListener productListener = new TheProductListener();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.notification.OriginCenter");

	public NotificationOriginCenter() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	private void processOrderState(Order order) {
		if (order.isTracking() && order.getPayer() != null) {
			fireNotification("order.state", new Recipient(order.getPayer()), order);
		}
	}

	private void processProductUpdated(Product product, Set<String> updatedFields) {
		if (updatedFields.contains("restockDate") && product.getStockInfo().isAvailable()) {
			final TrackingContext c = new TrackingContext(product.getId(), TrackingType.AVAILABILITY);

			final List<ProductTracking> tracks = trackingManager.searchEntities(c, null, null, null);
			for (ProductTracking tracking : tracks) {
				fireNotification("product.availability", new Recipient(tracking.getPersonEmail()), product);
				trackingManager.removeTracking(tracking.getId());
			}
		}

		if (updatedFields.contains("state") && product.getState() == ProductState.ACTIVE) {
			final TrackingContext c = new TrackingContext(product.getId(), TrackingType.DESCRIPTION);

			final List<ProductTracking> tracks = trackingManager.searchEntities(c, null, null, null);
			for (ProductTracking tracking : tracks) {
				fireNotification("product.description", new Recipient(tracking.getPersonEmail()), product);
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
			this.productManager.removeProductListener(productListener);
		}

		this.productManager = productManager;

		if (this.productManager != null) {
			this.productManager.addProductListener(productListener);
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

	private class TheProductListener implements ProductListener {
		private TheProductListener() {
		}

		@Override
		public void productCreated(Product product) {
		}

		@Override
		public void productUpdated(Product product, Set<String> updatedFields) {
			processProductUpdated(product, updatedFields);
		}

		@Override
		public void productRemoved(Product product) {
		}
	}
}
