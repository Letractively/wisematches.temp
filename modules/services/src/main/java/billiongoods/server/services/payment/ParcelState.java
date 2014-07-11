package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum ParcelState {
	PROCESSING,
	SHIPPING,
	SHIPPED,
	SUSPENDED,
	CANCELLED,
	CLOSED;

	private final String code;

	ParcelState() {
		this.code = name().toLowerCase();
	}

	public String getCode() {
		return code;
	}

}
