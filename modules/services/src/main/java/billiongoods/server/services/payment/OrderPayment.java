package billiongoods.server.services.payment;

/**
 * Instance of this class contains information about order payment, like id of transaction, unique
 * payment/refund token, payer name and so on.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderPayment {
	/**
	 * Internal unique transaction token.
	 *
	 * @return the payment token.
	 */
	String getToken();


	/**
	 * The payer code that is email by default.
	 *
	 * @return the payer code.
	 */
	String getPayer();

	/**
	 * Returns payer name provided by payment system.
	 *
	 * @return the payer name provided by payment system.
	 */
	String getPayerName();

	/**
	 * The payer notes for this order.
	 *
	 * @return the payer notes for this order.
	 */
	String getPayerNote();


	/**
	 * Returns unique refund operation id. If <code>null</code> the is no any refund.
	 *
	 * @return the unique refund operation id or <code>null</code> if there is no any.
	 */
	String getRefundId();

	/**
	 * Returns refund amount for the order.
	 *
	 * @return the refund amount for the order.
	 */
	Double getRefundAmount();


	/**
	 * Returns unique payment operation id.
	 *
	 * @return the unique payment operation id.
	 */
	String getPaymentId();

	/**
	 * Returns payment amount for the order.
	 *
	 * @return the  payment amount for the order.
	 */
	Double getPaymentAmount();
}
