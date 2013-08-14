package billiongoods.server.services.paypal.impl;

import billiongoods.server.services.paypal.PayPalQueryError;
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
public class HibernateTransaction implements PayPalTransaction {
    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "orderId", updatable = false)
    private Long orderId;

    @Column(name = "amount", updatable = false)
    private float amount;

    @Column(name = "shipment", updatable = false)
    private float shipment;

    @Column(name = "creationTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;

    @Column(name = "phase")
    @Enumerated(EnumType.ORDINAL)
    private TransactionPhase phase = TransactionPhase.CREATED;

    @Column(name = "resolution")
    @Enumerated(EnumType.ORDINAL)
    private TransactionResolution resolution;

    @Column(name = "payer")
    private String payer;

    @Column(name = "payerId")
    private String payerId;

    @Column(name = "payerPhone")
    private String payerPhone;

    @Column(name = "payerLastName")
    private String payerLastName;

    @Column(name = "payerFirstName")
    private String payerFirstName;

    @Column(name = "payerCountry")
    private String payerCountry;

    @Column(name = "checkoutStatus")
    private String checkoutStatus;


    @Column(name = "transactionId")
    private String transactionId;

    @Column(name = "transactionType")
    private String transactionType;

    @Column(name = "parentTransactionId")
    private String parentTransactionId;

    @Column(name = "paymentType")
    private String paymentType;

    @Column(name = "paymentStatus")
    private String paymentStatus;

    @Column(name = "paymentRequestId")
    private String paymentRequestId;

    @Column(name = "paymentDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

    @Column(name = "feeAmount")
    private float feeAmount;

    @Column(name = "grossAmount")
    private float grossAmount;

    @Column(name = "settleAmount")
    private float settleAmount;

    @Column(name = "taxAmount")
    private float taxAmount;

    @Column(name = "exchangeRate")
    private String exchangeRate;

    @Column(name = "reasonCode")
    private String reasonCode;

    @Column(name = "pendingReason")
    private String pendingReason;

    @Column(name = "holdDecision")
    private String holdDecision;

    @Column(name = "insuranceAmount")
    private String insuranceAmount;

    @Column(name = "invoicingTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date invoicingTime;

    @Column(name = "verificationTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date verificationTime;

    @Column(name = "confirmationTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmationTime;

    @Column(name = "finalizationTime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finalizationTime;

    @Column(name = "payerComment")
    private String payerComment;

    @Embedded
    private PayPalQueryError lastQueryError;

    @Deprecated
    HibernateTransaction() {
    }

    public HibernateTransaction(Long orderId, float amount, float shipment) {
        this.orderId = orderId;
        this.amount = amount;
        this.shipment = shipment;
        this.creationTime = new Date();
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
    public TransactionPhase getPhase() {
        return phase;
    }

    @Override
    public TransactionResolution getResolution() {
        return resolution;
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
    public Date getInvoicingTime() {
        return invoicingTime;
    }

    @Override
    public Date getVerificationTime() {
        return verificationTime;
    }

    @Override
    public String getPayer() {
        return payer;
    }

    @Override
    public String getPayerId() {
        return payerId;
    }

    @Override
    public String getPayerPhone() {
        return payerPhone;
    }

    @Override
    public String getPayerLastName() {
        return payerLastName;
    }

    @Override
    public String getPayerFirstName() {
        return payerFirstName;
    }

    @Override
    public String getPayerCountry() {
        return payerCountry;
    }

    @Override
    public String getCheckoutStatus() {
        return checkoutStatus;
    }

    @Override
    public Date getConfirmationTime() {
        return confirmationTime;
    }

    @Override
    public String getTransactionId() {
        return transactionId;
    }

    @Override
    public String getTransactionType() {
        return transactionType;
    }

    @Override
    public String getParentTransactionId() {
        return parentTransactionId;
    }

    @Override
    public String getPaymentType() {
        return paymentType;
    }

    @Override
    public String getPaymentStatus() {
        return paymentStatus;
    }

    @Override
    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    @Override
    public Date getPaymentDate() {
        return paymentDate;
    }

    @Override
    public float getFeeAmount() {
        return feeAmount;
    }

    @Override
    public float getGrossAmount() {
        return grossAmount;
    }

    @Override
    public float getSettleAmount() {
        return settleAmount;
    }

    @Override
    public float getTaxAmount() {
        return taxAmount;
    }

    @Override
    public String getExchangeRate() {
        return exchangeRate;
    }

    @Override
    public String getReasonCode() {
        return reasonCode;
    }

    @Override
    public String getPendingReason() {
        return pendingReason;
    }

    @Override
    public String getHoldDecision() {
        return holdDecision;
    }

    @Override
    public String getInsuranceAmount() {
        return insuranceAmount;
    }


    @Override
    public Date getCreationTime() {
        return creationTime;
    }

    @Override
    public String getPayerComment() {
        return payerComment;
    }

    @Override
    public Date getFinalizationTime() {
        return finalizationTime;
    }

    @Override
    public PayPalQueryError getLastQueryError() {
        return lastQueryError;
    }

    void setPhase(TransactionPhase phase) {
        this.phase = phase;
    }

    void setResolution(TransactionResolution resolution) {
        this.resolution = resolution;
    }

    void setToken(String token) {
        this.token = token;
    }

    void setPayer(String payer) {
        this.payer = payer;
    }

    void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    void setPayerPhone(String payerPhone) {
        this.payerPhone = payerPhone;
    }

    void setPayerLastName(String payerLastName) {
        this.payerLastName = payerLastName;
    }

    void setPayerFirstName(String payerFirstName) {
        this.payerFirstName = payerFirstName;
    }

    void setPayerCountry(String payerCountry) {
        this.payerCountry = payerCountry;
    }

    void setCheckoutStatus(String checkoutStatus) {
        this.checkoutStatus = checkoutStatus;
    }

    void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    void setParentTransactionId(String parentTransactionId) {
        this.parentTransactionId = parentTransactionId;
    }

    void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    void setFeeAmount(float feeAmount) {
        this.feeAmount = feeAmount;
    }

    void setGrossAmount(float grossAmount) {
        this.grossAmount = grossAmount;
    }

    void setSettleAmount(float settleAmount) {
        this.settleAmount = settleAmount;
    }

    void setTaxAmount(float taxAmount) {
        this.taxAmount = taxAmount;
    }

    void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    void setReasonCode(String reasonCode) {
        this.reasonCode = reasonCode;
    }

    void setPendingReason(String pendingReason) {
        this.pendingReason = pendingReason;
    }

    void setHoldDecision(String holdDecision) {
        this.holdDecision = holdDecision;
    }

    void setInsuranceAmount(String insuranceAmount) {
        this.insuranceAmount = insuranceAmount;
    }

    void setInvoicingTime(Date invoicingTime) {
        this.invoicingTime = invoicingTime;
    }

    void setVerificationTime(Date verificationTime) {
        this.verificationTime = verificationTime;
    }

    public void setConfirmationTime(Date confirmationTime) {
        this.confirmationTime = confirmationTime;
    }

    void setFinalizationTime(Date finalizationTime) {
        this.finalizationTime = finalizationTime;
    }

    void setPayerComment(String payerComment) {
        this.payerComment = payerComment;
    }

    void setLastQueryError(PayPalQueryError lastQueryError) {
        this.lastQueryError = lastQueryError;
    }
}