package billiongoods.server.services.payment.impl;

import billiongoods.core.Personality;
import billiongoods.core.account.Account;
import billiongoods.core.search.entity.EntitySearchManager;
import billiongoods.server.services.address.Address;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.coupon.Coupon;
import billiongoods.server.services.coupon.CouponManager;
import billiongoods.server.services.payment.*;
import billiongoods.server.warehouse.CategoryManager;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateOrderManager extends EntitySearchManager<Order, OrderContext, Void> implements OrderManager {
	private CouponManager couponManager;
	private ShipmentManager shipmentManager;
	private CategoryManager categoryManager;

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

		final double amount = basket.getAmount();
		final String couponId = basket.getCoupon();
		final Coupon coupon = couponManager.getCoupon(couponId);
		double discount = 0;
		if (coupon != null) {
			discount = coupon.getDiscount(basket, categoryManager.getCatalog());
		}
		final double shipmentCost = shipmentManager.getShipmentCost(basket, shipmentType);
		final Shipment shipment = new Shipment(shipmentCost, address, shipmentType);

		final HibernateOrder order = new HibernateOrder(person.getId(), amount, discount, couponId, shipment, track);
		session.save(order);

		int index = 0;
		final List<OrderItem> items = new ArrayList<>();
		for (BasketItem basketItem : basket.getBasketItems()) {
			items.add(new HibernateOrderItem(order, basketItem, index++));
		}
		order.setOrderItems(items);
		session.update(order);

		notifyOrderState(order, null);
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
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void reject(Long orderId, String payer, String paymentId, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		session.delete(order);

		log.info("Order has been rejected and removed from system: {}", orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void accept(Long orderId, String payer, String payerName, String payerNote, String paymentId) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.accept(payer, payerName, payerNote, paymentId);
		session.update(order);

		notifyOrderState(order, state);
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
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void suspend(Long orderId, Date resumeDate, String commentary) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.suspended(resumeDate, commentary);
		session.update(order);

		notifyOrderState(order, state);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void cancel(Long orderId, String refundId, String commentary) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.cancelled(refundId, commentary);
		session.update(order);

		notifyOrderState(order, state);
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

			} else {
				log.warn("Where is no order for token: {}", token);
			}
		} catch (Exception ex) {
			log.warn("Where is no order for token: {}", token);
		}
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void close(Long orderId, Date deliveryDate, String commentary) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getOrderState();
		order.close(deliveryDate, commentary);
		session.update(order);

		notifyOrderState(order, state);

		log.info("New state was changed to closed: {}", orderId);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void remove(Long orderId) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		if (order != null) {
			session.delete(order);
		}
		log.info("Order has been removed: {}", orderId);
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
	public OrdersSummary getOrdersSummary() {
		return getOrdersSummary(null);
	}

	@Override
	public OrdersSummary getOrdersSummary(Personality principal) {
		final Session session = sessionFactory.getCurrentSession();

		final ProjectionList projection = Projections.projectionList();
		projection.add(Projections.groupProperty("orderState"));
		projection.add(Projections.rowCount());

		final Criteria criteria = session.createCriteria(HibernateOrder.class);
		criteria.setProjection(projection);
		if (principal != null) {
			criteria.add(Restrictions.eq("buyer", principal.getId()));
		}

		final Map<OrderState, Integer> map = new HashMap<>();
		final List list = criteria.list();
		for (Object o : list) {
			final Object[] arr = (Object[]) o;
			map.put((OrderState) arr[0], ((Number) arr[1]).intValue());
		}
		return new OrdersSummary(map);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public int importAccountOrders(Account account) {
		if (account.getEmail() != null) {
			final Session session = sessionFactory.getCurrentSession();

			final Query query = session.createQuery("update billiongoods.server.services.payment.impl.HibernateOrder o set o.buyer = :principal where o.payer = :payer");
			query.setLong("principal", account.getId());
			query.setString("payer", account.getEmail());
			return query.executeUpdate();
		}
		return 0;
	}

	@Override
	protected void applyRestrictions(Criteria criteria, OrderContext context, Void filter) {
		if (context != null) {
			if (context.getOrderStates() != null) {
				criteria.add(Restrictions.in("orderState", context.getOrderStates()));
			}

			if (context.getPersonality() != null) {
				criteria.add(Restrictions.eq("buyer", context.getPersonality().getId()));
			}
		}
	}

	@Override
	protected void applyProjections(Criteria criteria, OrderContext context, Void filter) {
	}

	private void notifyOrderState(Order order, OrderState oldState) {
		log.info("Order state was changed from {} to {}: {}", oldState, order.getOrderState(), order.getId());

		for (OrderListener listener : listeners) {
			listener.orderStateChanged(order, oldState, order.getOrderState());
		}
	}

	public void setCouponManager(CouponManager couponManager) {
		this.couponManager = couponManager;
	}

	public void setCategoryManager(CategoryManager categoryManager) {
		this.categoryManager = categoryManager;
	}

	public void setShipmentManager(ShipmentManager shipmentManager) {
		this.shipmentManager = shipmentManager;
	}
}
