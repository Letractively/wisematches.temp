package billiongoods.server.services.payment.impl;

import billiongoods.server.services.payment.*;

import javax.persistence.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_order")
public class HibernateOrder implements Order {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "buyer")
	private Long buyer;

	@Column(name = "amount")
	private double amount;

	@Embedded
	private Shipment shipment;

	@Embedded
	private HibernateTimeline timeline;

	@Embedded
	private HibernateOrderPayment payment;

	@Embedded
	private HibernateOrderDiscount discount;

	@Column(name = "commentary")
	private String commentary;

	@Column(name = "timestamp")
	private LocalDateTime timestamp;

	@Column(name = "state")
	@Enumerated(EnumType.ORDINAL)
	private OrderState state = OrderState.NEW;

	@OrderBy("number")
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "pk.orderId", targetEntity = HibernateOrderItem.class, orphanRemoval = true)
	private List<OrderItem> orderItems;

	@OrderBy("id desc")
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderId", targetEntity = HibernateOrderLog.class)
	private List<OrderLog> orderLogs = new ArrayList<>();

	@OrderBy("number")
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderId", targetEntity = HibernateParcel.class)
	private List<Parcel> parcels = new ArrayList<>();

	HibernateOrder() {
	}

	public HibernateOrder(Long buyer, double amount, double discount, String coupon, Shipment shipment) {
		this.buyer = buyer;
		this.amount = amount;

		this.shipment = shipment;

		this.payment = new HibernateOrderPayment();
		this.discount = new HibernateOrderDiscount(discount, coupon);

		this.state = OrderState.NEW;
		this.timestamp = LocalDateTime.now();
		this.timeline = new HibernateTimeline(this.timestamp);
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Long getPersonId() {
		return buyer;
	}

	@Override
	public double getAmount() {
		return amount;
	}

	@Override
	public double getGrandTotal() {
		return amount + shipment.getAmount() - discount.getAmount();
	}

	@Override
	public String getCommentary() {
		return commentary;
	}

	@Override
	public Timeline getTimeline() {
		return timeline;
	}

	@Override
	public OrderState getState() {
		return state;
	}

	@Override
	public Shipment getShipment() {
		return shipment;
	}

	@Override
	public OrderPayment getPayment() {
		return payment;
	}

	@Override
	public OrderDiscount getDiscount() {
		return discount;
	}


	@Override
	public int getProductsCount() {
		int res = 0;
		for (OrderItem orderItem : orderItems) {
			res += orderItem.getQuantity();
		}
		return res;
	}

	@Override
	public Set<String> getNationalTracking() {
		final int size = parcels.size();
		if (size == 0) {
			return Collections.emptySet();
		}

		if (size == 1) {
			final Parcel parcel = parcels.get(0);
			final String chinaMailTracking = parcel.getChinaMailTracking();
			if (chinaMailTracking != null && !chinaMailTracking.isEmpty()) {
				return Collections.singleton(chinaMailTracking);
			} else {
				return Collections.emptySet();
			}
		} else {
			Set<String> res = new HashSet<>(size);
			for (Parcel parcel : parcels) {
				final String chinaMailTracking = parcel.getChinaMailTracking();
				if (chinaMailTracking != null && !chinaMailTracking.isEmpty()) {
					res.add(chinaMailTracking);
				}
			}
			return res;
		}
	}

	@Override
	public Set<String> getInternationalTracking() {
		final int size = parcels.size();
		if (size == 0) {
			return Collections.emptySet();
		}

		if (size == 1) {
			final Parcel parcel = parcels.get(0);
			final String internationalTracking = parcel.getInternationalTracking();
			if (internationalTracking != null && !internationalTracking.isEmpty()) {
				return Collections.singleton(internationalTracking);
			} else {
				return Collections.emptySet();
			}
		} else {
			Set<String> res = new HashSet<>(size);
			for (Parcel parcel : parcels) {
				final String internationalTracking = parcel.getInternationalTracking();
				if (internationalTracking != null && !internationalTracking.isEmpty()) {
					res.add(internationalTracking);
				}
			}
			return res;
		}
	}

	@Override
	public List<OrderItem> getItems() {
		return orderItems;
	}

	@Override
	public List<OrderLog> getLogs() {
		return orderLogs;
	}

	@Override
	public HibernateParcel getParcel(Long parcelId) {
		if (parcelId == null) {
			return null;
		}

		for (Parcel parcel : parcels) {
			if (parcelId.equals(parcel.getId())) {
				return (HibernateParcel) parcel;
			}
		}
		return null;
	}

	@Override
	public List<OrderItem> getItems(Parcel parcel) {
		if (parcel == null) {
			return orderItems;
		}
		return orderItems.stream().filter(parcel::contains).collect(Collectors.toList());
	}


	@Override
	public List<Parcel> getParcels() {
		return parcels;
	}

	@Override
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	void bill(String token) {
		this.payment.init(token);

		updateOrderState(OrderState.BILLING, null, token, null);
	}

	void accept(String paymentId, double amount, String payer, String payerName, String payerNote) {
		payment.processed(paymentId, amount, payer, payerName, payerNote);

		updateOrderState(OrderState.ACCEPTED, null, paymentId, null);
	}

	void reject(String paymentId, double amount, String payer, String payerName, String payerNote) {
		payment.processed(paymentId, amount, payer, payerName, payerNote);

		updateOrderState(OrderState.FAILED, null, null, null);
	}

	void failed(String errorCode, String reason) {
		this.commentary = reason;

		updateOrderState(OrderState.FAILED, null, errorCode, reason);
	}


	void process(HibernateParcel parcel) {
		updateOrderState(OrderState.PROCESSING, parcel, null, null);
	}


	void shipping(HibernateParcel parcel, String tracking, String commentary) {
		parcel.shipping(tracking);

		updateOrderState(calculateOrderState(), parcel, tracking, commentary);
	}

	void shipped(HibernateParcel parcel, String tracking, String commentary) {
		parcel.shipped(tracking);

		updateOrderState(calculateOrderState(), parcel, tracking, commentary);
	}

	void suspend(HibernateParcel parcel, LocalDateTime resume, String commentary) {
		parcel.suspend(resume);

		updateOrderState(calculateOrderState(), parcel, resume.toLocalDate().toString(), commentary);
	}

	void cancel(HibernateParcel parcel, String commentary) {
		parcel.cancel();

		updateOrderState(calculateOrderState(), parcel, null, commentary);
	}

	void closed(HibernateParcel parcel, LocalDateTime deliveryDate, String commentary) {
		parcel.closed(deliveryDate);
		updateOrderState(calculateOrderState(), parcel, deliveryDate.toLocalDate().toString(), commentary);
	}

	void modify(OrderItemChange[] changes) {
		double refundAmount = .0;

		// checks
		for (OrderItemChange change : changes) {
			if (getOrderItem(change.getItem()) == null) {
				throw new IllegalArgumentException("Unknown item number: " + change.getItem());
			}
		}

		for (OrderItemChange change : changes) {
			final HibernateOrderItem orderItem = getOrderItem(change.getItem());
			if (change.getQuantity() == 0) {
				this.orderItems.remove(orderItem);
				refundAmount += orderItem.getAmount() * orderItem.getQuantity();
			} else {
				int oldQuantity = orderItem.getQuantity();
				orderItem.updateQuantity(change.getQuantity());
				refundAmount += orderItem.getAmount() * (oldQuantity - change.getQuantity());
			}
		}

		this.amount -= refundAmount;
	}

	private HibernateOrderItem getOrderItem(Integer item) {
		for (OrderItem orderItem : orderItems) {
			if (item.equals(orderItem.getNumber())) {
				return (HibernateOrderItem) orderItem;
			}
		}
		return null;
	}

	void cancel(String note) {
		for (Parcel parcel : parcels) {
			((HibernateParcel) parcel).cancel();
		}

		updateOrderState(OrderState.CANCELLED, null, null, note);
	}

	void suspend(String note) {
		updateOrderState(OrderState.SUSPENDED, null, null, note);
	}

	void refund(String token, double amount, String note) {
		payment.refund(token, amount);

		orderLogs.add(new HibernateOrderLog(this, amount, token, note));
	}

	HibernateParcel createParcel(int number) {
		final HibernateParcel p = new HibernateParcel(this, number);
		parcels.add(p);
		return p;
	}

	private OrderState calculateOrderState() {
		final Set<ParcelState> set = parcels.stream().map(Parcel::getState).collect(Collectors.toSet());

		if (set.contains(ParcelState.PROCESSING) || set.contains(ParcelState.SUSPENDED)) {
			return OrderState.PROCESSING;
		}
		if (set.contains(ParcelState.SHIPPING)) {
			return OrderState.SHIPPING;
		}
		if (set.contains(ParcelState.SHIPPED)) {
			return OrderState.SHIPPED;
		}
		if (set.contains(ParcelState.CLOSED)) {
			return OrderState.CLOSED;
		}
		return OrderState.CANCELLED;
	}

	void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	private void updateOrderState(OrderState state, Parcel parcel, String parameter, String commentary) {
		OrderState oldState = this.state;

		this.state = state;
		this.timestamp = LocalDateTime.now(Clock.systemUTC());

		if (commentary != null && commentary.length() > 254) {
			commentary = commentary.substring(0, 254);
		}

		String comment = null;
		// comment was updated - change it.
		if (((this.commentary != null || commentary != null)) && ((this.commentary == null || !this.commentary.equals(commentary)))) {
			this.commentary = comment = commentary;
		}

		orderLogs.add(new HibernateOrderLog(this, parcel, parameter, comment));

		if (oldState != state) {
			updateTimeline(state);
		}
	}

	private void updateTimeline(OrderState state) {
		switch (state) {
			case ACCEPTED:
				if (timeline.getStarted() == null) {
					timeline.setStarted(LocalDateTime.now());
				}
				break;
			case PROCESSING:
				if (timeline.getProcessed() == null) {
					timeline.setProcessed(LocalDateTime.now());
				}
				break;
			case SHIPPED:
			case SHIPPING:
				if (timeline.getShipped() == null) {
					timeline.setShipped(LocalDateTime.now());
				}
				break;
			case CLOSED:
			case FAILED:
			case CANCELLED:
				if (timeline.getFinished() == null) {
					timeline.setFinished(LocalDateTime.now());
				}
				break;
		}
	}
}
