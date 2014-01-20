package billiongoods.server.services.payment.impl;

import billiongoods.server.services.address.Address;
import billiongoods.server.services.payment.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	public OrderState getOrderState() {
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
	public String getCommentary() {
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
	public int getItemsCount() {
		int res = 0;
		for (OrderItem orderItem : orderItems) {
			res += orderItem.getQuantity();
		}
		return res;
	}

	@Override
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	@Override
	public List<OrderLog> getOrderLogs() {
		return orderLogs;
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

	void processing(String referenceTracking, String commentary) {
		this.referenceTracking = referenceTracking;

		updateOrderState(OrderState.PROCESSING, commentary, referenceTracking);
	}

	void shipping(String chinaMailTracking, String commentary) {
		this.chinaMailTracking = chinaMailTracking;

		updateOrderState(OrderState.SHIPPING, commentary, chinaMailTracking);
	}

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

	private void updateOrderState(OrderState state, String commentary, String parameter) {
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
