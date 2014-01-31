package billiongoods.server.services.paypal;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PayPalTransaction {
	Long getId();

	Long getOrderId();

	String getToken();

	double getAmount();

	double getShipment();


	TransactionPhase getPhase();

	TransactionResolution getResolution();


	String getPayer();

	String getPayerId();

	String getPayerNote();

	String getPayerPhone();

	String getPayerName();

	String getPayerLastName();

	String getPayerFirstName();

	String getPayerCountry();

	String getCheckoutStatus();


	String getTransactionId();

	String getTransactionType();

	String getParentTransactionId();


	String getPaymentType();

	String getPaymentStatus();

	String getPaymentRequestId();

	Date getPaymentDate();


	double getFeeAmount();

	double getGrossAmount();

	double getSettleAmount();

	double getTaxAmount();

	String getExchangeRate();

	String getReasonCode();

	String getPendingReason();

	String getHoldDecision();

	String getInsuranceAmount();


	Date getCreationTime();

	Date getInvoicingTime();

	Date getVerificationTime();

	Date getConfirmationTime();

	Date getFinalizationTime();


	PayPalQueryError getLastQueryError();
}
