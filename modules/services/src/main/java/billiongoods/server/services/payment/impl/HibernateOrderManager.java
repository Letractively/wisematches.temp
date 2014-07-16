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

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateOrderManager extends EntitySearchManager<Order, OrderContext, Void> implements OrderManager {
	private CouponManager couponManager;
	private ShipmentManager shipmentManager;
	private CategoryManager categoryManager;

	private final Collection<OrderListener> orderListeners = new CopyOnWriteArrayList<>();
	private final Collection<ParcelListener> parcelListeners = new CopyOnWriteArrayList<>();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.order.OrderManager");

	public HibernateOrderManager() {
		super(HibernateOrder.class);
	}

	@Override
	public void addOrderListener(OrderListener l) {
		if (l != null) {
			orderListeners.add(l);
		}
	}

	@Override
	public void removeOrderListener(OrderListener l) {
		if (l != null) {
			orderListeners.remove(l);
		}
	}

	@Override
	public void addParcelListener(ParcelListener l) {
		if (l != null) {
			parcelListeners.add(l);
		}
	}

	@Override
	public void removeParcelListener(ParcelListener l) {
		if (l != null) {
			parcelListeners.remove(l);
		}
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order create(Personality person, Basket basket, Address address, ShipmentType shipmentType) {
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

		final HibernateOrder order = new HibernateOrder(person.getId(), amount, discount, couponId, shipment);
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
	public Order bill(Long orderId, String token) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getState();

		order.bill(token);
		session.update(order);

		notifyOrderState(order, state);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order accept(Long orderId, String paymentId, double amount, String payer, String payerName, String payerNote) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getState();
		order.accept(paymentId, amount, payer, payerName, payerNote);
		session.update(order);

		notifyOrderState(order, state);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order reject(Long orderId, String paymentId, double amount, String payer, String payerName, String payerNote) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getState();
		order.reject(paymentId, amount, payer, payerName, payerNote);
		session.delete(order);

		notifyOrderState(order, state);

		log.info("Order has been rejected and removed from system: {}", orderId);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order process(Long orderId, ParcelEntry... parcels) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getState();

		final Set<Integer> p = new HashSet<>();
		for (ParcelEntry parcel : parcels) {
			Collections.addAll(p, parcel.getProducts());
		}

		final List<OrderItem> items = order.getItems();
		for (OrderItem item : items) {
			final Integer id = item.getProduct().getId();
			if (!p.remove(id)) {
				throw new IllegalArgumentException("Products are not in any parcel: " + id);
			}
		}

		if (!p.isEmpty()) {
			throw new IllegalArgumentException("Unknown products were provided in parcels: " + p);
		}

		log.info("Processing order items: {} -> {}", orderId, Arrays.toString(parcels));

		for (ParcelEntry entry : parcels) {
			final HibernateParcel parcel = order.createParcel(entry.getNumber());
			final Long parcelId = (Long) session.save(parcel);

			for (OrderItem orderItem : order.getItems()) {
				for (Integer item : entry.getProducts()) {
					if (orderItem.getProduct().getId().equals(item)) {
						((HibernateOrderItem) orderItem).moveToParcel(parcelId);
					}
				}
			}
		}
		order.processing();
		session.update(order);

		notifyOrderState(order, state);

		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order refund(Long orderId, double amount, String refundId, String note) {
		throw new UnsupportedOperationException("TODO: Not implemented");
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order cancel(Long orderId, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState oldState = order.getState();
		order.cancel(note);
		session.update(order);

		notifyOrderState(order, oldState);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order suspend(Long orderId, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState oldState = order.getState();
		order.suspend(note);
		session.update(order);

		notifyOrderState(order, oldState);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order shipping(Long orderId, Long parcelId, String tracking, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState oldState = order.getState();
		order.shipping(parcelId, tracking, note);
		session.update(order);

		notifyOrderState(order, oldState);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order shipped(Long orderId, Long parcelId, String tracking, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState oldState = order.getState();
		order.shipped(parcelId, tracking, note);
		session.update(order);

		notifyOrderState(order, oldState);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order cancel(Long orderId, Long parcelId, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState oldState = order.getState();
		order.cancel(parcelId, note);
		session.update(order);

		notifyOrderState(order, oldState);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order close(Long orderId, Long parcelId, LocalDateTime delivered, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState oldState = order.getState();
		order.closed(parcelId, delivered, note);
		session.update(order);

		notifyOrderState(order, oldState);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order suspend(Long orderId, Long parcelId, LocalDateTime resume, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState oldState = order.getState();
		order.suspend(parcelId, resume, note);
		session.update(order);

		notifyOrderState(order, oldState);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order failed(Long orderId, String reason) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getState();
		order.failed(reason);
		session.update(order);

		notifyOrderState(order, state);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order failed(String token, String reason) {
		final Session session = sessionFactory.getCurrentSession();

		try {
			final HibernateOrder order = getByToken(token);
			if (order != null) {
				final OrderState state = order.getState();
				order.failed(reason);
				session.update(order);

				notifyOrderState(order, state);
				return order;
			} else {
				log.warn("Where is no order for token: {}", token);
			}
		} catch (Exception ex) {
			log.warn("Where is no order for token: {}", token);
		}
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order remove(Long orderId) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		if (order != null) {
			session.delete(order);
		}
		log.info("Order has been removed: {}", orderId);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void setOrderTracking(Order order, boolean enable) {
		throw new UnsupportedOperationException("Deprecated");
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

		final Query query = session.createQuery("from billiongoods.server.services.payment.impl.HibernateOrder o where o.payment.token=:token");
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
		final OrderState newState = order.getState();
		if (newState == oldState) {
			return;
		}

		log.info("Order state was changed from {} to {}: {}", oldState, newState, order.getId());

		for (OrderListener listener : orderListeners) {
			listener.orderStateChanged(order, oldState, newState);
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
