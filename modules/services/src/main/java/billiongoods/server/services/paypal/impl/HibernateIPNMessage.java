package billiongoods.server.services.paypal.impl;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "paypal_ipn_message")
public class HibernateIPNMessage {
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column("txn_id")
    private String txnId;

    @Column("txn_type")
    private String txnType;

    @Column("verify_sign")
    private String verifySign;

    @Column("business")
    private String business;

    @Column("charset")
    private String charset;

    @Column("custom")
    private String custom;

    @Column("ipn_track_id")
    private String ipnTrackId;

    @Column("notify_version")
    private String notifyVersion;

    @Column("parent_txn_id")
    private String parentTxnId;

    @Column("receipt_id")
    private String receiptId;

    @Column("receiver_email")
    private String receiverEmail;

    @Column("receiver_id")
    private String receiverId;

    @Column("resend")
    private String resend;

    @Column("residence_country")
    private String residenceCountry;

    @Column("test_ipn")
    private String test;

    @Column("transaction_subject")
    private String transactionSubject;

    @Column("message")
    private String message;

    @Deprecated
    HibernateIPNMessage() {
    }

    public HibernateIPNMessage(Map<String, String[]> map) {
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
                    field.set(this, map.get(annotation.name())[0]);
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("No access to field: " + field);
                }
            }
        }
        this.message = map.toString();
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
