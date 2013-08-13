package billiongoods.server.services.payment.impl;

import billiongoods.server.services.payment.OrderLog;
import billiongoods.server.services.payment.OrderState;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_order_log")
public class HibernateOrderLog implements OrderLog {
    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "orderId", updatable = false)
    private Long orderId;

    @Column(name = "code", updatable = false)
    private String code;

    @Column(name = "timestamp", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Column(name = "parameter", updatable = false)
    private String parameter;

    @Column(name = "orderState", updatable = false)
    @Enumerated(EnumType.ORDINAL)
    private OrderState orderState;

    @Deprecated
    HibernateOrderLog() {
    }

    public HibernateOrderLog(Long orderId, String code, String parameter, OrderState orderState) {
        this.orderId = orderId;
        this.code = code;
        this.timestamp = new Date();
        this.parameter = parameter;
        this.orderState = orderState;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public Date getTimeStamp() {
        return timestamp;
    }

    @Override
    public String getParameter() {
        return parameter;
    }

    @Override
    public OrderState getOrderState() {
        return orderState;
    }

    @Override
    public String toString() {
        return "HibernateOrderLog{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", timestamp=" + timestamp +
                ", parameter='" + parameter + '\'' +
                ", orderState=" + orderState +
                '}';
    }
}
