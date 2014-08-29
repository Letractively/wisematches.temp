package billiongoods.server.services.payment.impl;

import billiongoods.server.services.payment.*;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderExpirationCenter implements OrderExpirationManager, InitializingBean {
	private OrderManager orderManager;
	private TaskScheduler taskScheduler;
	private SessionFactory sessionFactory;
	private TransactionTemplate transactionTemplate;

	private final Lock lock = new ReentrantLock();

	private final Map<OrderState, Integer> days = new HashMap<>();
	private final Map<Long, ScheduledFuture<?>> futureMap = new HashMap<>();

	private final TheOrderListener orderListener = new TheOrderListener();
	private final Collection<OrderExpirationListener> listeners = new CopyOnWriteArrayList<>();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.order.OrderExpirationCenter");

	public OrderExpirationCenter() {
		days.put(OrderState.ACCEPTED, 1);
		days.put(OrderState.PROCESSING, 3);
		days.put(OrderState.SHIPPING, 3);
		days.put(OrderState.SUSPENDED, 7);
		days.put(OrderState.SHIPPED, 90);
	}

	@Override
	public void addOrderExpirationListener(OrderExpirationListener l) {
		if (l != null) {
			listeners.add(l);
		}
	}

	@Override
	public void removeOrderExpirationListener(OrderExpirationListener l) {
		if (l != null) {
			listeners.remove(l);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				final List<Order> orders = orderManager.searchEntities(new OrderContext(days.keySet()), null, null, null);

				log.info("Initializing order expiration center: {}", orders.size());
				orders.forEach(OrderExpirationCenter.this::scheduleOrderCheck);
			}
		});
	}

	private void scheduleOrderCheck(Order order) {
		lock.lock();
		try {
			final Long orderId = order.getId();
			final OrderState state = order.getState();

			final ScheduledFuture<?> future = futureMap.get(orderId); // cancel exist before next steps
			if (future != null) {
				log.info("Cancel previous future: {} ", future);
				future.cancel(false);
			}

			final Integer integer = days.get(state);
			if (integer == null) {
				return;
			}

			final Temporal now = LocalDateTime.now();
			final Temporal timestamp = order.getTimestamp();
			final long period = TimeUnit.DAYS.toMillis(integer.longValue());
			final long duration = Duration.between(timestamp, now).toMillis();

			if (duration >= period && state == OrderState.SHIPPED) { // out of date shipped
				log.info("Shipped order has been expired. Do termination...");

				processOrderCheck(order);
			} else {
				final long initial = period - (duration % period);
				log.info("Schedule order check: id - {}, state - {}, initial - {}, now - {}, timestamp - {}", orderId, state, initial, now, timestamp);

				final PeriodicTrigger trigger = new PeriodicTrigger(period, TimeUnit.MILLISECONDS);
				trigger.setFixedRate(true);
				trigger.setInitialDelay(initial);

				final ScheduledFuture<?> schedule = taskScheduler.schedule(new TheTerminator(orderId), trigger);
				futureMap.put(orderId, schedule);
			}
		} finally {
			lock.unlock();
		}
	}

	public void processOrderCheck(Long id) {
		final Order order = orderManager.getOrder(id);
		if (order != null) {
			processOrderCheck(order);
		}
	}

	public void processOrderCheck(Order order) {
		final OrderState state = order.getState();
		if (!days.containsKey(state)) {
			log.info("Order #{} state {} was changed from last time. Invalid state now.", order.getId(), state);
		}

		if (state == OrderState.SHIPPED) {
			log.info("Close order by timeout: {}", order.getId());

			final LocalDateTime now = LocalDateTime.now();
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					order.getParcels().stream().filter(parcel -> parcel.getState() == ParcelState.SHIPPED).forEach(parcel -> {
						orderManager.close(order.getId(), parcel.getId(), now, "Заказ автоматически закрыт по прошествие " + days.get(OrderState.SHIPPED) + " дней с момента отправки.");
					});
				}
			});
		} else {
			log.info("Notify expiring order: {}", order.getId());

			for (OrderExpirationListener listener : listeners) {
				listener.orderExpiring(order);
			}
		}
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

	public void setTaskScheduler(TaskScheduler taskScheduler) {
		this.taskScheduler = taskScheduler;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	private class TheTerminator implements Runnable {
		private final Long id;

		public TheTerminator(Long id) {
			this.id = id;
		}

		@Override
		public void run() {
			processOrderCheck(id);
		}
	}

	private class TheOrderListener implements OrderListener {
		private TheOrderListener() {
		}

		@Override
		public void orderRefund(Order order, String token, double amount) {
		}

		@Override
		public void orderStateChanged(Order order, OrderState oldState, OrderState newState) {
			scheduleOrderCheck(order);
		}

		@Override
		public void orderContentChanged(Order order, OrderItemChange... changes) {
		}
	}
}
