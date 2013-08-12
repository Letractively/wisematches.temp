package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PayPalQueryException extends PayPalException {
    private final PayPalQueryError queryError;

    public PayPalQueryException(PayPalQueryError queryError) {
        super("PayPal query error received: " + queryError);
        this.queryError = queryError;
    }

    public PayPalQueryError getQueryError() {
        return queryError;
    }
}
