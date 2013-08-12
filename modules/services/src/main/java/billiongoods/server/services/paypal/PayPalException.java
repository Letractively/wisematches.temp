package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PayPalException extends Exception {
    public PayPalException() {
    }

    public PayPalException(String message) {
        super(message);
    }

    public PayPalException(String message, Throwable cause) {
        super(message, cause);
    }
}
