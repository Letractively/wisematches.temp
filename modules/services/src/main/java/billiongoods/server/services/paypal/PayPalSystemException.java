package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PayPalSystemException extends PayPalException {
	public static final String SYSTEM_ERROR_CODE = "SYSTEM";

	public PayPalSystemException(String tnxId, String message) {
		super(tnxId, SYSTEM_ERROR_CODE, message);
	}

	public PayPalSystemException(String tnxId, String message, Throwable cause) {
		super(tnxId, SYSTEM_ERROR_CODE, message, cause);
	}
}
