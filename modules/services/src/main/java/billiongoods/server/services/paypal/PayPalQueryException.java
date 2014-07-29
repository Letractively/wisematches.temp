package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PayPalQueryException extends PayPalException {
	private final PayPalQueryError queryError;

	public PayPalQueryException(String tnxId, PayPalQueryError queryError) {
		super(tnxId, queryError.getCode(), queryError.getLongMessage());
		this.queryError = queryError;
	}

	public PayPalQueryError getQueryError() {
		return queryError;
	}
}
