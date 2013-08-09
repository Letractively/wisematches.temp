package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum PayPalMode {
	LIVE("https://www.paypal.com/cgi-bin/webscr"),
	SANDBOX("https://www.sandbox.paypal.com/cgi-bin/webscr");

	private final String endpoint;

	PayPalMode(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getCode() {
		return name().toLowerCase();
	}

	public String getPayPalEndpoint() {
		return endpoint;
	}
}
