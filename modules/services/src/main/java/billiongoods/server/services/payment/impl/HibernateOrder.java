package billiongoods.server.services.payment.impl;

import billiongoods.server.services.payment.Address;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderItem;
import billiongoods.server.services.payment.OrderState;

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

    @Column(name = "code")
    private String code;

    @Embedded
    private HibernateAddress address;

    @Column(name = "state")
    @Enumerated(EnumType.ORDINAL)
    private OrderState orderState = OrderState.NEW;

    @JoinColumn(name = "orderId")
    @OrderColumn(name = "number")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, targetEntity = HibernateOrderItem.class)
    private List<OrderItem> orderItems;

    public HibernateOrder() {
    }

    public HibernateOrder(Long buyer, String code, HibernateAddress address) {
        this.buyer = buyer;
        this.code = code;
        this.address = address;
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
    public String getCode() {
        return code;
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
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
