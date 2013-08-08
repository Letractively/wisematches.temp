package billiongoods.server.test.notify;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum NotificationScope {
	/**
	 * Indicates that notification should be sent only to internal publisher.
	 */
	INTERNAL(true, false),
	/**
	 * Indicates that notification should be sent only to external publisher.
	 */
	EXTERNAL(false, true),
	/**
	 * Indicates that notification should be sent to both transports.
	 */
	GLOBAL(true, true);

	private final boolean internal;
	private final boolean external;

	private NotificationScope(boolean internal, boolean external) {
		this.internal = internal;
		this.external = external;
	}

	public boolean isInternal() {
		return internal;
	}

	public boolean isExternal() {
		return external;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("NotificationScope");
		sb.append("{internal=").append(internal);
		sb.append(", external=").append(external);
		sb.append('}');
		return sb.toString();
	}
}
