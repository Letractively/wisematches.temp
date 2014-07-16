package billiongoods.server.services.payment.impl;

import billiongoods.server.services.payment.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

	@Column(name = "amount", updatable = false)
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

	@Column(name = "state")
	@Enumerated(EnumType.ORDINAL)
	private OrderState state = OrderState.NEW;

	@OrderBy("number")
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "pk.orderId", targetEntity = HibernateOrderItem.class)
	private List<OrderItem> orderItems;

	@OrderBy("timestamp desc")
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
		this.timeline = new HibernateTimeline(LocalDateTime.now());
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
	public List<OrderItem> getItems() {
		return orderItems;
	}

	@Override
	public List<OrderLog> getLogs() {
		return orderLogs;
	}

	@Override
	public HibernateParcel getParcel(Long parcelId) {
		for (Parcel parcel : parcels) {
			if (parcelId.equals(parcel.getId())) {
				return (HibernateParcel) parcel;
			}
		}
		return null;
	}

	@Override
	public List<Parcel> getParcels() {
		return parcels;
	}


	@Override
	public List<OrderItem> getItems(Parcel parcel) {
		if (parcel == null) {
			return orderItems;
		}
		return orderItems.stream().filter(parcel::contains).collect(Collectors.toList());
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

		updateOrderState(OrderState.CANCELLED, null, paymentId, null);
	}

	void failed(String reason) {
		this.commentary = reason;

		updateOrderState(OrderState.FAILED, null, null, reason);
	}


	void processing() {
		for (Parcel parcel : parcels) {
			updateOrderState(OrderState.PROCESSING, parcel, null, null);
		}
	}


	void shipping(Long parcelId, String tracking, String commentary) {
		final HibernateParcel parcel = getParcel(parcelId);
		if (parcel == null) {
			throw new IllegalArgumentException("Unknown parcel " + parcelId);
		}

		parcel.shipping(tracking);

		updateOrderState(calculateOrderState(), parcel, tracking, commentary);
	}

	void shipped(Long parcelId, String tracking, String commentary) {
		final HibernateParcel parcel = getParcel(parcelId);
		if (parcel == null) {
			throw new IllegalArgumentException("Unknown parcel " + parcelId);
		}

		parcel.shipped(tracking);

		updateOrderState(calculateOrderState(), parcel, tracking, commentary);
	}

	void suspend(Long parcelId, LocalDateTime resume, String commentary) {
		final HibernateParcel parcel = getParcel(parcelId);
		if (parcel == null) {
			throw new IllegalArgumentException("Unknown parcel " + parcelId);
		}

		parcel.suspend(resume);

		updateOrderState(calculateOrderState(), parcel, null, commentary);
	}

	void cancel(Long parcelId, String commentary) {
		final HibernateParcel parcel = getParcel(parcelId);
		if (parcel == null) {
			throw new IllegalArgumentException("Unknown parcel " + parcelId);
		}

		parcel.cancel();

		updateOrderState(calculateOrderState(), parcel, null, commentary);
	}


	void cancel(String note) {
		updateOrderState(OrderState.CANCELLED, null, null, note);
	}

	void suspend(String note) {
		updateOrderState(OrderState.SUSPENDED, null, null, note);
	}

	void closed(Long parcelId, LocalDateTime deliveryDate, String commentary) {
		final HibernateParcel parcel = getParcel(parcelId);
		if (parcel == null) {
			throw new IllegalArgumentException("Unknown parcel " + parcelId);
		}

		parcel.closed(deliveryDate);

		updateOrderState(calculateOrderState(), parcel, deliveryDate.toString(), commentary);
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

		if (oldState != state) {
			updateTimeline(state);
		}

		if (commentary != null && commentary.length() > 254) {
			commentary = commentary.substring(0, 254);
		}

		String comment = null;
		// comment was updated - change it.
		if (((this.commentary != null || commentary != null)) && ((this.commentary == null || !this.commentary.equals(commentary)))) {
			this.commentary = comment = commentary;
		}

		orderLogs.add(new HibernateOrderLog(this, parcel, parameter, comment));
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
