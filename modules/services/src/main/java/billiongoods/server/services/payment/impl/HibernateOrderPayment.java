package billiongoods.server.services.payment.impl;

import billiongoods.server.services.payment.OrderPayment;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class HibernateOrderPayment implements OrderPayment {
	@Column(name = "token")
	private String token;

	@Column(name = "payer")
	private String payer;

	@Column(name = "payerName")
	private String payerName;

	@Column(name = "payerNote")
	private String payerNote;

	@Column(name = "paymentId")
	private String paymentId;

	@Column(name = "paymentAmount")
	private Double paymentAmount;

	@Column(name = "refundId")
	private String refundId;

	@Column(name = "refundAmount")
	private Double refundAmount;

	public HibernateOrderPayment() {
	}

	@Override
	public String getToken() {
		return token;
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
	public String getRefundId() {
		return refundId;
	}

	@Override
	public Double getRefundAmount() {
		return refundAmount;
	}

	@Override
	public String getPaymentId() {
		return paymentId;
	}

	@Override
	public Double getPaymentAmount() {
		return paymentAmount;
	}

	void init(String token) {
		this.token = token;
	}

	void processed(String paymentId, double amount, String payer, String payerName, String payerNote) {
		this.paymentId = paymentId;
		this.paymentAmount = amount;
		this.payer = payer;
		this.payerName = payerName;
		this.payerNote = payerNote;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateOrderPayment{");
		sb.append("token='").append(token).append('\'');
		sb.append(", payer='").append(payer).append('\'');
		sb.append(", payerName='").append(payerName).append('\'');
		sb.append(", payerNote='").append(payerNote).append('\'');
		sb.append(", paymentId='").append(paymentId).append('\'');
		sb.append(", paymentAmount=").append(paymentAmount);
		sb.append(", refundId='").append(refundId).append('\'');
		sb.append(", refundAmount=").append(refundAmount);
		sb.append('}');
		return sb.toString();
	}

}
