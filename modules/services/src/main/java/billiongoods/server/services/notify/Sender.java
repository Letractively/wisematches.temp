package billiongoods.server.services.notify;

import billiongoods.server.services.ServerDescriptor;

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

	private final String userInfo;

	Sender(String userInfo) {
		this.userInfo = userInfo;
	}

	/**
	 * Returns user info for this sender
	 *
	 * @return the sender' user info
	 */
	public String getUserInfo() {
		return userInfo;
	}

	public String getMailAddress(ServerDescriptor descriptor) {
		return userInfo + "@" + descriptor.getMailHostName();
	}
}