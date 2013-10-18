package billiongoods.server.services.notify;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum Sender {
	/**
	 * From address is bugs reporter.
	 */
	SUPPORT("support"),

	/**
	 * This is abstract e-mail notification.
	 */
	UNDEFINED("noreply"),

	/**
	 * Mail was sent from accounts support team.
	 */
	ACCOUNTS("account-noreply"),

	/**
	 * Mail was sent from server to any monitoring address. This email address must be used only
	 * for internal monitoring and alerts email sent to the same host mailboxes.
	 */
	SERVER("server");

	private final String code;

	Sender(String code) {
		this.code = code;
	}

	/**
	 * Returns user info for this sender
	 *
	 * @return the sender' user info
	 */
	public String getCode() {
		return code;
	}
}