package billiongoods.server.services.payment.impl;

import billiongoods.server.services.address.Address;
import billiongoods.server.services.payment.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
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

	@Column(name = "token")
	private String token;

	@Column(name = "amount", updatable = false)
	private double amount;

	@Column(name = "discount", updatable = false)
	private double discount;

	@Column(name = "shipment", updatable = false)
	private double shipmentAmount;

	@Column(name = "shipmentType", updatable = false)
	@Enumerated(EnumType.ORDINAL)
	private ShipmentType shipmentType;

	@Column(name = "coupon", updatable = false)
	private String coupon;

	@Embedded
	private Address shipmentAddress;

	@Column(name = "created", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "shipped")
	@Temporal(TemporalType.TIMESTAMP)
	private Date shipped;

	@Column(name = "closed")
	@Temporal(TemporalType.TIMESTAMP)
	private Date closed;

	@Column(name = "timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@Column(name = "payer")
	private String payer;

	@Column(name = "payerName")
	private String payerName;

	@Column(name = "payerNote")
	private String payerNote;

	@Column(name = "paymentId")
	private String paymentId;

	@Column(name = "tracking")
	private boolean tracking;

	@Column(name = "exceptedResume")
	@Temporal(TemporalType.TIMESTAMP)
	private Date exceptedResume;

	@Column(name = "refundToken")
	private String refundToken;

	@Column(name = "commentary")
	private String commentary;

	@Column(name = "referenceTracking")
	private String referenceTracking;

	@Column(name = "chinaMailTracking")
	private String chinaMailTracking;

	@Column(name = "internationalTracking")
	private String internationalTracking;

	@Column(name = "state")
	@Enumerated(EnumType.ORDINAL)
	private OrderState orderState = OrderState.NEW;

	@OrderBy("number")
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "pk.orderId", targetEntity = HibernateOrderItem.class)
	private List<OrderItem> orderItems;

	@OrderBy("timestamp desc")
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderId", targetEntity = HibernateOrderLog.class)
	private List<OrderLog> orderLogs = new ArrayList<>();

	@OrderBy("number")
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderId", targetEntity = HibernateOrderParcel.class)
	private List<OrderParcel> orderParcels = new ArrayList<>();

	@Transient
	private Shipment shipment = null;

	HibernateOrder() {
	}

	public HibernateOrder(Long buyer, double amount, double discount, String coupon, Shipment shipment, boolean tracking) {
		this.buyer = buyer;
		this.amount = amount;
		this.coupon = coupon;
		this.discount = discount;
		this.shipmentAmount = shipment.getAmount();
		this.shipmentType = shipment.getType();
		this.shipmentAddress = new Address(shipment.getAddress());
		this.tracking = tracking;
		this.orderState = OrderState.NEW;
		timestamp = new Date();
		created = new Date();
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
	public String getToken() {
		return token;
	}

	@Override
	public double getAmount() {
		return amount;
	}

	@Override
	public String getCoupon() {
		return coupon;
	}

	@Override
	public double getDiscount() {
		return discount;
	}

	@Override
	public Shipment getShipment() {
		if (shipment == null) {
			shipment = new Shipment(shipmentAmount, shipmentAddress, shipmentType);
		}
		return shipment;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public Date getCreated() {
		return created;
	}

	@Override
	public Date getShipped() {
		return shipped;
	}

	@Override
	public Date getClosed() {
		return closed;
	}

	@Override
    public OrderState getState() {
        return orderState;
	}

	@Override
	public String getPayer() {
		return payer;
	}

	@Override
	public String getPayerName() {
		return payerName;
	}

	@Override
	public String getPayerNote() {
		return payerNote;
	}

	@Override
	public String getPaymentId() {
		return paymentId;
	}

	@Override
	public boolean isTracking() {
		return tracking;
	}

	@Override
    public String getSuspendMessage() {
        return commentary;
	}

	@Override
	public String getReferenceTracking() {
		return referenceTracking;
	}

	@Override
	public String getChinaMailTracking() {
		return chinaMailTracking;
	}

	@Override
	public String getInternationalTracking() {
		return internationalTracking;
	}

	@Override
	public String getRefundToken() {
		return refundToken;
	}

	@Override
	public Date getExpectedResume() {
		return exceptedResume;
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
	public HibernateOrderParcel getParcel(int number) {
		for (OrderParcel orderParcel : orderParcels) {
			if (orderParcel.getNumber() == number) {
				return (HibernateOrderParcel) orderParcel;
			}
		}
		return null;
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
    public List<OrderParcel> getParcels() {
        return orderParcels;
	}

	@Override
    public List<OrderItem> getItems(OrderParcel parcel) {
        return orderItems.stream().filter(item -> (parcel == null && !item.isRegistered()) || (parcel != null && parcel.contains(item))).collect(Collectors.toList());
	}

	void bill(String token) {
		this.token = token;

		updateOrderState(OrderState.BILLING, null, token);
	}

	void accept(String payer, String payerName, String payerNote, String paymentId) {
		this.payer = payer;
		this.payerName = payerName;
		this.payerNote = payerNote;
		this.paymentId = paymentId;

		updateOrderState(OrderState.ACCEPTED, null, paymentId);
	}

	void addParcel(HibernateOrderParcel parcel) {
		orderParcels.add(parcel);
	}

	void removeParcel(HibernateOrderParcel parcel) {
		orderParcels.remove(parcel);
	}

	void shipping(int parcel, String tracking, String commentary) {
		final HibernateOrderParcel parcel1 = getParcel(parcel);
		if (parcel1 == null) {
			throw new IllegalArgumentException("Unknown parcel " + parcel);
		}

		parcel1.shipping(tracking);

		updateOrderState(calculateOrderState(), parcel, tracking, commentary);
	}

	void shipped(int parcel, String tracking, String commentary) {
		final HibernateOrderParcel parcel1 = getParcel(parcel);
		if (parcel1 == null) {
			throw new IllegalArgumentException("Unknown parcel " + parcel);
		}

		parcel1.shipped(tracking);

		updateOrderState(calculateOrderState(), parcel, tracking, commentary);
	}

	void closed(int parcel, String tracking, String commentary) {
		final HibernateOrderParcel parcel1 = getParcel(parcel);
		if (parcel1 == null) {
			throw new IllegalArgumentException("Unknown parcel " + parcel);
		}

		parcel1.closed();

		updateOrderState(calculateOrderState(), parcel, tracking, commentary);
	}

	private OrderState calculateOrderState() {
		final Set<ParcelState> set = orderParcels.stream().map(OrderParcel::getState).collect(Collectors.toSet());

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


	@Deprecated
	void processing(String referenceTracking, String commentary) {
		this.referenceTracking = referenceTracking;

		updateOrderState(OrderState.PROCESSING, commentary, referenceTracking);
	}

	@Deprecated
	void shipping(String chinaMailTracking, String commentary) {
		this.chinaMailTracking = chinaMailTracking;

		updateOrderState(OrderState.SHIPPING, commentary, chinaMailTracking);
	}

	@Deprecated
	void shipped(String internationalTracking, String commentary) {
		this.shipped = new Date();
		this.internationalTracking = internationalTracking;

		updateOrderState(OrderState.SHIPPED, commentary, internationalTracking);
	}

	void failed(String commentary) {
		updateOrderState(OrderState.FAILED, commentary, null);
	}

	void suspended(Date exceptedResume, String commentary) {
		this.exceptedResume = exceptedResume;

		updateOrderState(OrderState.SUSPENDED, commentary, exceptedResume != null ? String.valueOf(exceptedResume.getTime()) : null);
	}

	void cancelled(String refundId, String commentary) {
		this.refundToken = refundId;

		updateOrderState(OrderState.CANCELLED, commentary, refundId);
	}

	void close(Date deliveryDate, String commentary) {
		this.closed = new Date();

		updateOrderState(OrderState.CLOSED, commentary, deliveryDate != null ? String.valueOf(deliveryDate.getTime()) : null);
	}

	void setTracking(boolean tracking) {
		this.tracking = tracking;
	}

	void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	private void updateOrderState(OrderState state, int parcel, String parameter, String commentary) {
		// TODO: add parcel number here
		this.timestamp = new Date();
		this.orderState = state;

		if (state != OrderState.SUSPENDED) {
			exceptedResume = null;
		}


		if (commentary != null && commentary.length() > 254) {
			commentary = commentary.substring(0, 254);
		}

		String comment = null;
		// comment was updated - change it.
		if (((this.commentary != null || commentary != null)) && ((this.commentary == null || !this.commentary.equals(commentary)))) {
			this.commentary = comment = commentary;
		}

		orderLogs.add(new HibernateOrderLog(id, parameter, comment, state));
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateOrder{");
		sb.append("id=").append(id);
		sb.append(", buyer=").append(buyer);
		sb.append(", token='").append(token).append('\'');
		sb.append(", amount=").append(amount);
		sb.append(", shipmentAmount=").append(shipmentAmount);
		sb.append(", shipmentType=").append(shipmentType);
		sb.append(", shipmentAddress=").append(shipmentAddress);
		sb.append(", created=").append(created);
		sb.append(", timestamp=").append(timestamp);
		sb.append(", payer='").append(payer).append('\'');
		sb.append(", payerNote='").append(payerNote).append('\'');
		sb.append(", paymentId='").append(paymentId).append('\'');
		sb.append(", tracking=").append(tracking);
		sb.append(", exceptedResume=").append(exceptedResume);
		sb.append(", refundToken='").append(refundToken).append('\'');
		sb.append(", commentary='").append(commentary).append('\'');
		sb.append(", referenceTracking='").append(referenceTracking).append('\'');
		sb.append(", chinaMailTracking='").append(chinaMailTracking).append('\'');
		sb.append(", internationalTracking='").append(internationalTracking).append('\'');
		sb.append(", orderState=").append(orderState);
		sb.append(", orderItems=").append(orderItems);
		sb.append(", orderLogs=").append(orderLogs);
		sb.append(", shipment=").append(shipment);
		sb.append('}');
		return sb.toString();
	}
}
