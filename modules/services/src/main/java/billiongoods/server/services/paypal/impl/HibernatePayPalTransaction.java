package billiongoods.server.services.paypal.impl;

import billiongoods.server.services.paypal.PayPalTransaction;
import billiongoods.server.services.paypal.TransactionPhase;
import billiongoods.server.services.paypal.TransactionResolution;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "paypal_transaction")
public class HibernatePayPalTransaction implements PayPalTransaction {
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column("orderId")
    private Long orderId;

    @Column("timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    @Column("phase")
    @Enumerated(EnumType.ORDINAL)
    private TransactionPhase phase = TransactionPhase.NEW;

    @Column("resolution")
    @Enumerated(EnumType.ORDINAL)
    private TransactionResolution resolution;

    public HibernatePayPalTransaction(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Long getOrderId() {
        return orderId;
    }

    @Override
    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public TransactionPhase getPhase() {
        return phase;
    }

    @Override
    public TransactionResolution getResolution() {
        return resolution;
    }


    void setPhase(TransactionPhase phase) {
        this.phase = phase;
    }

    void setResolution(TransactionResolution resolution) {
        this.resolution = resolution;
    }
}
