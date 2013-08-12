package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class PayPalException extends Exception {
    protected PayPalException(String message) {
        super(message);
    }

    protected PayPalException(String message, Throwable cause) {
        super(message, cause);
    }
}
