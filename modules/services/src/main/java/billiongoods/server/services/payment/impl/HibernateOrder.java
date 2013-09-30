package billiongoods.server.services.payment.impl;

import billiongoods.server.services.basket.Basket;
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

	@Column(name = "shipment", updatable = false)
	private double shipmentAmount;

	@Column(name = "shipmentType", updatable = false)
	@Enumerated(EnumType.ORDINAL)
	private ShipmentType shipmentType;

	@Embedded
	private HibernateAddress shipmentAddress;

	@Column(name = "creationTime", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTime;

	@Column(name = "timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@Column(name = "payer")
	private String payer;

	@Column(name = "payerNote")
	private String payerNote;

	@Column(name = "paymentId")
	private String paymentId;

	@Column(name = "tracking")
	private boolean tracking;

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

	@Deprecated
	HibernateOrder() {
	}

	public HibernateOrder(Long buyer, Basket basket, Shipment shipment, boolean tracking) {
		this.buyer = buyer;
		this.amount = basket.getAmount();
		this.shipmentAmount = shipment.getAmount();
		this.shipmentType = shipment.getType();
		this.shipmentAddress = new HibernateAddress(shipment.getAddress());
		this.tracking = tracking;
		this.orderState = OrderState.NEW;
		timestamp = new Date();
		creationTime = new Date();
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Long getBuyer() {
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
	public Date getCreationTime() {
		return creationTime;
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
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	@Override
	public List<OrderLog> getOrderLogs() {
		return orderLogs;
	}

	void bill(String token) {
		this.token = token;

		updateOrderState("billing", token, null, OrderState.BILLING);
	}

	void accept(String payer, String paymentId, String note) {
		this.payer = payer;
		this.payerNote = note;
		this.paymentId = paymentId;

		updateOrderState("accepted", paymentId, null, OrderState.ACCEPTED);
	}

	void reject(String payer, String paymentId, String note) {
		this.payer = payer;
		this.payerNote = note;
		this.paymentId = paymentId;

		updateOrderState("rejected", paymentId, null, OrderState.REJECTED);
	}

	void processing(String referenceTracking, String commentary) {
		this.referenceTracking = referenceTracking;

		updateOrderState("processing", referenceTracking, commentary, OrderState.PROCESSING);
	}

	void shipping(String chinaMailTracking, String commentary) {
		this.chinaMailTracking = chinaMailTracking;

		updateOrderState("shipping", chinaMailTracking, commentary, OrderState.SHIPPING);
	}

	void shipped(String internationalTracking, String commentary) {
		this.internationalTracking = internationalTracking;

		updateOrderState("shipped", internationalTracking, commentary, OrderState.SHIPPED);
	}

	void failed(String commentary) {
		updateOrderState("failed", null, commentary, OrderState.FAILED);
	}

	void suspended(String commentary) {
		updateOrderState("suspended", null, commentary, OrderState.SUSPENDED);
	}

	void cancelled(String commentary) {
		updateOrderState("cancelled", null, commentary, OrderState.CANCELLED);
	}

	void setTracking(boolean tracking) {
		this.tracking = tracking;
	}

	void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	private void updateOrderState(String code, String parameter, String commentary, OrderState orderState) {
		this.timestamp = new Date();
		this.orderState = orderState;

		if (commentary != null && commentary.length() > 254) {
			this.commentary = commentary.substring(0, 254);
		} else {
			this.commentary = commentary;
		}
		orderLogs.add(new HibernateOrderLog(id, code, parameter, this.commentary, orderState));
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateOrder{");
		sb.append("id=").append(id);
		sb.append(", buyer=").append(buyer);
		sb.append(", token='").append(token).append('\'');
		sb.append(", amount=").append(amount);
		sb.append(", shipment=").append(shipment);
		sb.append(", shipmentType=").append(shipmentType);
		sb.append(", payer='").append(payer).append('\'');
		sb.append(", tracking=").append(tracking);
		sb.append(", payerNote='").append(payerNote).append('\'');
		sb.append(", commentary='").append(commentary).append('\'');
		sb.append(", referenceTracking='").append(referenceTracking).append('\'');
		sb.append(", chinaMailTracking='").append(chinaMailTracking).append('\'');
		sb.append(", internationalTracking='").append(internationalTracking).append('\'');
		sb.append(", shipmentAddress=").append(shipmentAddress);
		sb.append(", orderState=").append(orderState);
		sb.append(", orderItems=").append(orderItems);
		sb.append(", orderLogs=").append(orderLogs);
		sb.append('}');
		return sb.toString();
	}
}
