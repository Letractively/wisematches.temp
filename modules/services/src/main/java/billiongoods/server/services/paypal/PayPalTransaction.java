package billiongoods.server.services.paypal;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PayPalTransaction {
	Long getId();

	Long getOrderId();

	String getToken();

	float getAmount();

	float getShipment();

	Date getTimestamp();

	TransactionPhase getPhase();

	TransactionResolution getResolution();

	String getAckSet();

	Date getTimestampSet();

	String getAckGet();

	Date getTimestampGet();

	String getPayer();

	String getPayerId();

	String getPayerPhone();

	String getPayerLastName();

	String getPayerFirstName();

	String getPayerCountry();

	String getCheckoutStatus();

	String getAckDo();

	Date getTimestampDo();

	String getTransactionId();

	String getTransactionType();

	String getParentTransactionId();

	String getPaymentType();

	String getPaymentStatus();

	String getPaymentRequestId();

	Date getPaymentDate();

	float getFeeAmount();

	float getGrossAmount();

	float getSettleAmount();

	float getTaxAmount();

	String getExchangeRate();

	String getReasonCode();

	String getPendingReason();

	String getHoldDecision();

	String getInsuranceAmount();
}
