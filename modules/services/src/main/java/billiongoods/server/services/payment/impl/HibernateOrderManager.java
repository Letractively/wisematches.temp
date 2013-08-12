package billiongoods.server.services.payment.impl;

import billiongoods.core.Personality;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.payment.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateOrderManager implements OrderManager {
	private SessionFactory sessionFactory;

	private final Collection<OrderListener> listeners = new CopyOnWriteArrayList<>();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.order.OrderManager");

	public HibernateOrderManager() {
	}

	@Override
	public void addOrderListener(OrderListener l) {
		if (l != null) {
			listeners.add(l);
		}
	}

	@Override
	public void removeOrderListener(OrderListener l) {
		if (l != null) {
			listeners.remove(l);
		}
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order create(Personality person, Basket basket, Shipment shipment, boolean track) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = new HibernateOrder(person.getId(), basket, shipment, track);
		session.save(order);

		final List<OrderItem> items = new ArrayList<>();
		for (BasketItem basketItem : basket.getBasketItems()) {
			items.add(new HibernateOrderItem(order, basketItem));
		}
		order.setOrderItems(items);
		session.update(order);

		notifyOrderState(order, null);

		log.info("New order has been placed: " + order.getId());

		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void bill(Long orderId, String token) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();

		order.setToken(token);
		order.setOrderState(OrderState.BILLING);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to billing: " + orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void accept(Long orderId, String payer) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.setPayer(payer);
		order.setOrderState(OrderState.ACCEPTED);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to accepted: " + orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void reject(Long orderId, String payer) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.setPayer(payer);
		order.setOrderState(OrderState.REJECTED);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to rejected: " + orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void processing(Long orderId, String number) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.setReferenceTracking(number);
		order.setOrderState(OrderState.PROCESSING);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to processing: " + orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void shipping(Long orderId, String number) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.setChinaMailTracking(number);
		order.setOrderState(OrderState.SHIPPING);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to shipping: " + orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void shipped(Long orderId, String number) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.setInternationalTracking(number);
		order.setOrderState(OrderState.SHIPPED);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to shipped: " + orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void failed(Long orderId, String reason) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.setComment(reason);
		order.setOrderState(OrderState.FAILED);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to failed: " + orderId);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public HibernateOrder getOrder(Long id) {
		return (HibernateOrder) sessionFactory.getCurrentSession().get(HibernateOrder.class, id);
	}

	private void notifyOrderState(Order order, OrderState oldState) {
		for (OrderListener listener : listeners) {
			listener.orderStateChange(order, oldState, order.getOrderState());
		}
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
