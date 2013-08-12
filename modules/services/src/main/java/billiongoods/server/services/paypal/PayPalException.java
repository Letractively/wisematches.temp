package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PayPalException extends Exception {
	private final PayPalTransaction transaction;

	public PayPalException(PayPalTransaction transaction, String message, Throwable cause) {
		super(message, cause);
		this.transaction = transaction;
	}

	public PayPalTransaction getTransaction() {
		return transaction;
	}
}
