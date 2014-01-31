package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class PayPalException extends Exception {
    private final String tnxId;

    protected PayPalException(String tnxId, String message) {
        super(message);
        this.tnxId = tnxId;
    }

    protected PayPalException(String tnxId, String message, Throwable cause) {
        super(message, cause);
        this.tnxId = tnxId;
    }

    public String getTnxId() {
        return tnxId;
    }
}
