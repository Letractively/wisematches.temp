package billiongoods.server.services.payment.impl;

import billiongoods.core.Personality;
import billiongoods.core.search.entity.EntitySearchManager;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.payment.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateOrderManager extends EntitySearchManager<Order, OrderContext, Void> implements OrderManager {
	private ShipmentManager shipmentManager;

	private final Collection<OrderListener> listeners = new CopyOnWriteArrayList<>();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.order.OrderManager");

	public HibernateOrderManager() {
		super(HibernateOrder.class);
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
	public Order create(Personality person, Basket basket, Address address, ShipmentType shipmentType, boolean track) {
		final Session session = sessionFactory.getCurrentSession();

		final double shipmentCost = shipmentManager.getShipmentCost(basket, shipmentType);
		final Shipment shipment = new Shipment(shipmentCost, address, shipmentType);

		final HibernateOrder order = new HibernateOrder(person.getId(), basket, shipment, track);
		session.save(order);

		int index = 0;
		final List<OrderItem> items = new ArrayList<>();
		for (BasketItem basketItem : basket.getBasketItems()) {
			items.add(new HibernateOrderItem(order, basketItem, index++));
		}
		order.setOrderItems(items);
		session.update(order);

		notifyOrderState(order, null);

		log.info("New order has been placed: {}", order.getId());

		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void bill(Long orderId, String token) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();

		order.bill(token);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to billing: {}", orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void accept(Long orderId, String payer, String paymentId, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.accept(payer, paymentId, note);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to accepted: {}", orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void reject(Long orderId, String payer, String paymentId, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.reject(payer, paymentId, note);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to rejected: {}", orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void processing(Long orderId, String number, String commentary) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.processing(number, commentary);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to processing: {}", orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void shipping(Long orderId, String number, String commentary) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.shipping(number, commentary);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to shipping: {}", orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void shipped(Long orderId, String number, String commentary) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.shipped(number, commentary);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to shipped: {}", orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void suspended(Long orderId, Date resumeDate, String commentary) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.suspended(resumeDate, commentary);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to shipped: {}", orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void cancelled(Long orderId, String refundId, String commentary) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.cancelled(refundId, commentary);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to shipped: {}", orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void failed(Long orderId, String reason) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.failed(reason);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to failed: {}", orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void failed(String token, String reason) {
		final Session session = sessionFactory.getCurrentSession();

		try {
			final HibernateOrder order = getByToken(token);
			if (order != null) {
				final OrderState state = order.getOrderState();
				order.failed(reason);
				session.update(order);

				notifyOrderState(order, state);

				log.info("New state was changed to failed: {}", order.getId());
			} else {
				log.warn("Where is no order for token: {}", token);
			}
		} catch (Exception ex) {
			log.warn("Where is no order for token: {}", token);
		}
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void setOrderTracking(Order order, boolean enable) {
		final HibernateOrder ho = (HibernateOrder) order;
		ho.setTracking(enable);
		sessionFactory.getCurrentSession().update(ho);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public HibernateOrder getOrder(Long id) {
		return (HibernateOrder) sessionFactory.getCurrentSession().get(HibernateOrder.class, id);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public HibernateOrder getByToken(String token) {
		final Session session = sessionFactory.getCurrentSession();

		final Query query = session.createQuery("from billiongoods.server.services.payment.impl.HibernateOrder o where o.token=:token");
		query.setParameter("token", token);
		return (HibernateOrder) query.uniqueResult();
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public Order getByReference(String reference) {
		final Session session = sessionFactory.getCurrentSession();

		final Query query = session.createQuery("from billiongoods.server.services.payment.impl.HibernateOrder o where o.referenceTracking=:ref");
		query.setParameter("ref", reference);
		return (HibernateOrder) query.uniqueResult();
	}

	@Override
	protected void applyRestrictions(Criteria criteria, OrderContext context, Void filter) {
	}

	@Override
	protected void applyProjections(Criteria criteria, OrderContext context, Void filter) {
		if (context != null) {
			if (context.getOrderState() != null) {
				criteria.add(Restrictions.eq("orderState", context.getOrderState()));
			}
		}
	}

	private void notifyOrderState(Order order, OrderState oldState) {
		for (OrderListener listener : listeners) {
			listener.orderStateChange(order, oldState, order.getOrderState());
		}
	}

	public void setShipmentManager(ShipmentManager shipmentManager) {
		this.shipmentManager = shipmentManager;
	}
}
