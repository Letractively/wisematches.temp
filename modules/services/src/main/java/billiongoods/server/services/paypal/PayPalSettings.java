package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PayPalSettings {
	private String user;
	private String password;
	private String signature;
	private PayPalMode mode;

	public PayPalSettings(String user, String password, String signature, PayPalMode mode) {
		this.user = user;
		this.password = password;
		this.signature = signature;
		this.mode = mode;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getSignature() {
		return signature;
	}

	public PayPalMode getMode() {
		return mode;
	}
}
