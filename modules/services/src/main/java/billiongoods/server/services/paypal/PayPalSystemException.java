package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PayPalSystemException extends PayPalException {
    protected PayPalSystemException(String tnxId, String message) {
        super(tnxId, message);
    }

    protected PayPalSystemException(String tnxId, String message, Throwable cause) {
        super(tnxId, message, cause);
    }
}
