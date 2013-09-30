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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class NotificationOriginCenter implements BreakingDayListener, InitializingBean {
	private OrderManager orderManager;

	private NotificationService notificationService;

	private final OrderListener orderListener = new TheOrderListener();

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

	private void fireNotification(String code, Recipient recipient, Object context) {
		try {
			notificationService.raiseNotification("order.state", recipient, Sender.UNDEFINED, context);
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

}
