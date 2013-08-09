package billiongoods.server.services.paypal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class CheckoutToken {
	private final String token;
	private final String build;
	private final String correlationID;
	private final String timestamp;
	private final String version;

	public CheckoutToken(String token, String build, String correlationID, String timestamp, String version) {
		this.token = token;
		this.build = build;
		this.correlationID = correlationID;
		this.timestamp = timestamp;
		this.version = version;
	}

	public String getToken() {
		return token;
	}

	public String getBuild() {
		return build;
	}

	public String getCorrelationID() {
		return correlationID;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("CheckoutToken{");
		sb.append("token='").append(token).append('\'');
		sb.append(", build='").append(build).append('\'');
		sb.append(", correlationID='").append(correlationID).append('\'');
		sb.append(", timestamp='").append(timestamp).append('\'');
		sb.append(", version='").append(version).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
