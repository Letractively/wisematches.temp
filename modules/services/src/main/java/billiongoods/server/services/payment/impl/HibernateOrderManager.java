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
import java.util.stream.Collectors;

/**
 * TODO: parcel listeners are not enabled!
 *
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
			final HibernateOrderItem e = new HibernateOrderItem(order, basketItem, index++);
			items.add(e);
		}
		order.setOrderItems(items);
		session.update(order);

		notifyStateChange(order, null, null, null);
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

		notifyStateChange(order, state, null, null);
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

		notifyStateChange(order, state, null, null);
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

		notifyStateChange(order, state, null, null);

		log.info("Order has been rejected and removed from system: {}", orderId);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order process(Long orderId, ParcelEntry... entries) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		final OrderState state = order.getState();

		final List<Integer> processingIds = new ArrayList<>();
		for (ParcelEntry entry : entries) {
			Collections.addAll(processingIds, entry.getItems());
		}

		final List<Integer> processedIds = new ArrayList<>();
		for (Parcel parcel : order.getParcels()) {
			processedIds.addAll(order.getItems(parcel).stream().map(OrderItem::getNumber).collect(Collectors.toList()));
		}

		final List<Integer> allIds = order.getItems().stream().map(OrderItem::getNumber).collect(Collectors.toList());

		if (!allIds.containsAll(processingIds)) {
			processedIds.removeAll(allIds);
			throw new IllegalArgumentException("Some products not in the order: " + processedIds);
		}

		allIds.removeAll(processedIds);
		allIds.removeAll(processingIds);
		if (!allIds.isEmpty()) {
			throw new IllegalArgumentException("Unknown products were provided in parcels: " + allIds);
		}

		log.info("Processing order items: {} -> {}", orderId, Arrays.toString(entries));

		for (ParcelEntry entry : entries) {
			final HibernateParcel parcel = order.createParcel(entry.getNumber());
			final Long parcelId = (Long) session.save(parcel);

			for (OrderItem orderItem : order.getItems()) {
				for (Integer parcelItem : entry.getItems()) {
					if (orderItem.getNumber().equals(parcelItem)) {
						((HibernateOrderItem) orderItem).moveToParcel(parcelId);
					}
				}
			}

			order.process(parcel);
		}
		session.update(order);

		notifyStateChange(order, state, null, null);

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

		notifyStateChange(order, oldState, null, null);
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

		notifyStateChange(order, oldState, null, null);

		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order shipping(Long orderId, Long parcelId, String tracking, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		if (order == null) {
			throw new IllegalArgumentException("Unknown order id: " + orderId);
		}

		final HibernateParcel parcel = order.getParcel(parcelId);
		if (parcel == null) {
			throw new IllegalArgumentException("Unknown parcel id: " + parcelId);
		}


		final OrderState oldOState = order.getState();
		final ParcelState oldPState = parcel.getState();

		order.shipping(parcel, tracking, note);
		session.update(order);

		notifyStateChange(order, oldOState, parcel, oldPState);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order shipped(Long orderId, Long parcelId, String tracking, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		if (order == null) {
			throw new IllegalArgumentException("Unknown order id: " + orderId);
		}

		final HibernateParcel parcel = order.getParcel(parcelId);
		if (parcel == null) {
			throw new IllegalArgumentException("Unknown parcel id: " + parcelId);
		}


		final OrderState oldOState = order.getState();
		final ParcelState oldPState = parcel.getState();

		order.shipped(parcel, tracking, note);
		session.update(order);

		notifyStateChange(order, oldOState, parcel, oldPState);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order cancel(Long orderId, Long parcelId, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		if (order == null) {
			throw new IllegalArgumentException("Unknown order id: " + orderId);
		}

		final HibernateParcel parcel = order.getParcel(parcelId);
		if (parcel == null) {
			throw new IllegalArgumentException("Unknown parcel id: " + parcelId);
		}


		final OrderState oldOState = order.getState();
		final ParcelState oldPState = parcel.getState();

		order.cancel(parcel, note);
		session.update(order);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order close(Long orderId, Long parcelId, LocalDateTime delivered, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		if (order == null) {
			throw new IllegalArgumentException("Unknown order id: " + orderId);
		}

		final HibernateParcel parcel = order.getParcel(parcelId);
		if (parcel == null) {
			throw new IllegalArgumentException("Unknown parcel id: " + parcelId);
		}


		final OrderState oldOState = order.getState();
		final ParcelState oldPState = parcel.getState();

		order.closed(parcel, delivered, note);
		session.update(order);

		notifyStateChange(order, oldOState, parcel, oldPState);
		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order suspend(Long orderId, Long parcelId, LocalDateTime resume, String note) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = getOrder(orderId);
		if (order == null) {
			throw new IllegalArgumentException("Unknown order id: " + orderId);
		}

		final HibernateParcel parcel = order.getParcel(parcelId);
		if (parcel == null) {
			throw new IllegalArgumentException("Unknown parcel id: " + parcelId);
		}


		final OrderState oldOState = order.getState();
		final ParcelState oldPState = parcel.getState();

		order.suspend(parcel, resume, note);
		session.update(order);

		notifyStateChange(order, oldOState, parcel, oldPState);
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

		notifyStateChange(order, state, null, null);
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

				notifyStateChange(order, state, null, null);
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
		throw new UnsupportedOperationException("Commented");
//		final Session session = sessionFactory.getCurrentSession();
//
//		final Query query = session.createQuery("from billiongoods.server.services.payment.impl.HibernateOrder o where o.referenceTracking=:ref");
//		query.setParameter("ref", reference);
//		return (HibernateOrder) query.uniqueResult();
	}

	@Override
	public OrdersSummary getOrdersSummary() {
		return getOrdersSummary(null);
	}

	@Override
	public OrdersSummary getOrdersSummary(Personality principal) {
		final Session session = sessionFactory.getCurrentSession();

		final ProjectionList projection = Projections.projectionList();
		projection.add(Projections.groupProperty("state"));
		projection.add(Projections.rowCount());

		final Criteria criteria = session.createCriteria(HibernateOrder.class);
		criteria.setProjection(projection);
		if (principal != null) {
			criteria.add(Restrictions.eq("payment.buyer", principal.getId()));
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

			final Query query = session.createQuery("update billiongoods.server.services.payment.impl.HibernateOrder o set o.buyer = :principal where o.payment.payer = :payer");
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
				criteria.add(Restrictions.in("state", context.getOrderStates()));
			}

			if (context.getPersonality() != null) {
				criteria.add(Restrictions.eq("payment.buyer", context.getPersonality().getId()));
			}
		}
	}

	@Override
	protected void applyProjections(Criteria criteria, OrderContext context, Void filter) {
	}

	private void notifyStateChange(Order order, OrderState oldState, Parcel parcel, ParcelState oldParcelState) {
		if (parcel != null) {
			notifyParcelState(order, parcel, oldParcelState);
		}
		notifyOrderState(order, oldState);
	}

	private void notifyParcelState(Order order, Parcel parcel, ParcelState oldState) {
		final ParcelState newState = parcel.getState();
		if (newState == oldState) {
			return;
		}

		log.info("Parcel state was changed from {} to {}: {}:{}", oldState, newState, order.getId(), parcel.getId());

		for (ParcelListener listener : parcelListeners) {
			listener.parcelStateChanged(order, parcel, oldState, newState);
		}
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