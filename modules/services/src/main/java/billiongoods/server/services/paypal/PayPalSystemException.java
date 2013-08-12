package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PayPalSystemException extends PayPalException {
    protected PayPalSystemException(String message) {
        super(message);
    }

    protected PayPalSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
