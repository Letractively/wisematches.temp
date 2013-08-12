package billiongoods.server.services.paypal.impl;

import billiongoods.server.services.paypal.PayPalMessage;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "paypal_ipn_message")
public class HibernateIPNMessage implements PayPalMessage {
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "txn_id")
    private String txnId;

    @Column(name = "txn_type")
    private String txnType;

    @Column(name = "verify_sign")
    private String verifySign;

    @Column(name = "business")
    private String business;

    @Column(name = "charset")
    private String charset;

    @Column(name = "custom")
    private String custom;

    @Column(name = "ipn_track_id")
    private String ipnTrackId;

    @Column(name = "notify_version")
    private String notifyVersion;

    @Column(name = "parent_txn_id")
    private String parentTxnId;

    @Column(name = "receipt_id")
    private String receiptId;

    @Column(name = "receiver_email")
    private String receiverEmail;

    @Column(name = "receiver_id")
    private String receiverId;

    @Column(name = "resend")
    private String resend;

    @Column(name = "residence_country")
    private String residenceCountry;

    @Column(name = "test_ipn")
    private String test;

    @Column(name = "transaction_subject")
    private String transactionSubject;

    @Column(name = "message")
    private String message;

    @Deprecated
    HibernateIPNMessage() {
    }

    public HibernateIPNMessage(Map<String, String> values) {
        final Field[] fields = getClass().getFields();
        for (Field field : fields) {
            Column annotation = null;
            final Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
            for (Annotation declaredAnnotation : declaredAnnotations) {
                if (declaredAnnotation instanceof Column) {
                    annotation = (Column) declaredAnnotation;
                    break;
                }
            }

            if (annotation != null) {
                try {
                    field.set(this, values.get(annotation.name()));
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("No access to field: " + field);
                }
            }
        }
        this.message = values.toString();
    }

    public Long getId() {
        return id;
    }

    public String getTxnId() {
        return txnId;
    }

    public String getTxnType() {
        return txnType;
    }

    public String getVerifySign() {
        return verifySign;
    }

    public String getBusiness() {
        return business;
    }

    public String getCharset() {
        return charset;
    }

    public String getCustom() {
        return custom;
    }

    public String getIpnTrackId() {
        return ipnTrackId;
    }

    public String getNotifyVersion() {
        return notifyVersion;
    }

    public String getParentTxnId() {
        return parentTxnId;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getResend() {
        return resend;
    }

    public String getResidenceCountry() {
        return residenceCountry;
    }

    public boolean isTest() {
        return Boolean.parseBoolean(test);
    }

    public String getTransactionSubject() {
        return transactionSubject;
    }

    public String getMessage() {
        return message;
    }
}
