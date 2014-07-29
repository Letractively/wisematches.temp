package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class PayPalException extends Exception {
	private final String tnxId;
	private final String code;

	protected PayPalException(String tnxId, String code, String message) {
		super(message);
		this.tnxId = tnxId;
		this.code = code;
	}

	protected PayPalException(String tnxId, String code, String message, Throwable cause) {
		super(message, cause);
		this.tnxId = tnxId;
		this.code = code;
	}

	public String getTnxId() {
		return tnxId;
	}

	public String getCode() {
		return code;
	}

	public String getFullMessage() {
		return "ERROR-" + code + ": " + super.getMessage();
	}
}