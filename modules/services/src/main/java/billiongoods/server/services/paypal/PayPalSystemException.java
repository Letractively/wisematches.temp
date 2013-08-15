package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PayPalSystemException extends PayPalException {
	public PayPalSystemException(String tnxId, String message) {
		super(tnxId, tnxId + ": " + message);
	}

	public PayPalSystemException(String tnxId, String message, Throwable cause) {
		super(tnxId, tnxId + ": " + message, cause);
	}
}
