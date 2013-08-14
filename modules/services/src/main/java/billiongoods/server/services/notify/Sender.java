package billiongoods.server.services.notify;

import billiongoods.server.services.ServerDescriptor;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum Sender {
	/**
	 * This is abstract e-mail notification.
	 */
	UNDEFINED("noreply"),

	/**
	 * From address is bugs reporter.
	 */
	SUPPORT("support"),

	/**
	 * Mail was sent from accounts support team.
	 */
	ACCOUNTS("account-noreply");

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