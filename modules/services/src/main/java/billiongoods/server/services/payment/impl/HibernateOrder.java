package billiongoods.server.services.payment.impl;

import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.payment.*;

import javax.persistence.*;
import java.util.ArrayList;
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

    @OrderColumn(name = "number")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "pk.orderId", targetEntity = HibernateOrderItem.class)
    private List<OrderItem> orderItems;

    @OrderBy("timestamp desc")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderId", targetEntity = HibernateOrderLog.class)
    private List<OrderLog> orderLogs = new ArrayList<>();

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

    @Override
    public List<OrderLog> getOrderLogs() {
        return orderLogs;
    }

    void bill(String token) {
        this.token = token;
        this.orderState = OrderState.BILLING;
        orderLogs.add(new HibernateOrderLog(id, "billing", token, OrderState.BILLING));
    }

    void accept(String payer) {
        this.payer = payer;
        this.orderState = OrderState.ACCEPTED;
        orderLogs.add(new HibernateOrderLog(id, "accepted", payer, OrderState.ACCEPTED));
    }

    void reject(String payer) {
        this.payer = payer;
        this.orderState = OrderState.REJECTED;
        orderLogs.add(new HibernateOrderLog(id, "rejected", payer, OrderState.REJECTED));
    }

    void processing(String referenceTracking) {
        this.referenceTracking = referenceTracking;
        this.orderState = OrderState.PROCESSING;
        orderLogs.add(new HibernateOrderLog(id, "processing", referenceTracking, OrderState.PROCESSING));
    }

    void shipping(String chinaMailTracking) {
        this.chinaMailTracking = chinaMailTracking;
        this.orderState = OrderState.SHIPPING;
        orderLogs.add(new HibernateOrderLog(id, "shipping", chinaMailTracking, OrderState.SHIPPING));
    }

    void shipped(String internationalTracking) {
        this.internationalTracking = internationalTracking;
        this.orderState = OrderState.SHIPPED;
        orderLogs.add(new HibernateOrderLog(id, "shipped", internationalTracking, OrderState.SHIPPED));
    }

    void failed(String comment) {
        if (comment != null && comment.length() > 254) {
            this.comment = comment.substring(0, 254);
        } else {
            this.comment = comment;
        }
        this.orderState = OrderState.FAILED;
        orderLogs.add(new HibernateOrderLog(id, "failed", comment, OrderState.FAILED));
    }

    void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
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
