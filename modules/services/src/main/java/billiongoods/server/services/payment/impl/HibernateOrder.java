package billiongoods.server.services.payment.impl;

import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.payment.*;

import javax.persistence.*;
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

	@Column(name = "amount")
	private float amount;

	@Column(name = "shipment")
	private float shipment;

	@Column(name = "shipmentType")
	@Enumerated(EnumType.ORDINAL)
	private ShipmentType shipmentType;

	@Column(name = "payer")
	private String payer;

	@Column(name = "tracking")
	private boolean tracking;

	@Column(name = "comment")
	private String comment;

	@Column(name = "referenceTracking")
	private String referenceTracking;

	@Column(name = "chinaMailTracking")
	private String chinaMailTracking;

	@Column(name = "internationalTracking")
	private String internationalTracking;

	@Embedded
	private HibernateAddress address;

	@Column(name = "state")
	@Enumerated(EnumType.ORDINAL)
	private OrderState orderState = OrderState.NEW;

	@JoinColumn(name = "orderId")
	@OrderColumn(name = "number")
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = HibernateOrderItem.class)
	private List<OrderItem> orderItems;

	@Deprecated
	HibernateOrder() {
	}

	public HibernateOrder(Long buyer, Basket basket, Shipment shipment, boolean tracking) {
		this.buyer = buyer;
		this.amount = basket.getAmount();
		this.shipment = shipment.getAmount();
		this.shipmentType = shipment.getType();
		this.address = new HibernateAddress(shipment.getAddress());
		this.tracking = tracking;
		this.orderState = OrderState.NEW;
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
	public float getAmount() {
		return amount;
	}

	@Override
	public float getShipment() {
		return shipment;
	}

	@Override
	public ShipmentType getShipmentType() {
		return shipmentType;
	}

	@Override
	public Address getAddress() {
		return address;
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
	public boolean isTracking() {
		return tracking;
	}

	@Override
	public String getComment() {
		return comment;
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

	void setToken(String token) {
		this.token = token;
	}

	void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	void setPayer(String payer) {
		this.payer = payer;
	}

	void setComment(String comment) {
		this.comment = comment;
	}

	void setOrderState(OrderState orderState) {
		this.orderState = orderState;
	}

	void setReferenceTracking(String referenceTracking) {
		this.referenceTracking = referenceTracking;
	}

	void setChinaMailTracking(String chinaMailTracking) {
		this.chinaMailTracking = chinaMailTracking;
	}

	void setInternationalTracking(String internationalTracking) {
		this.internationalTracking = internationalTracking;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateOrder{");
		sb.append("orderItems=").append(orderItems);
		sb.append(", id=").append(id);
		sb.append(", buyer=").append(buyer);
		sb.append(", token='").append(token).append('\'');
		sb.append(", amount=").append(amount);
		sb.append(", shipment=").append(shipment);
		sb.append(", shipmentType=").append(shipmentType);
		sb.append(", address=").append(address);
		sb.append(", orderState=").append(orderState);
		sb.append('}');
		return sb.toString();
	}
}
