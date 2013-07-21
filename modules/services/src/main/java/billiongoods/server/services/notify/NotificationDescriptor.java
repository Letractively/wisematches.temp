package billiongoods.server.services.notify;

/**
 * {@code NotificationDescriptor} describes a notification and how it should be processed inside
 * {@code NotificationSettingsManager}.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class NotificationDescriptor implements Comparable<NotificationDescriptor> {
    private final String code;
    private final NotificationScope scope;

    /**
     * Creates new descriptor with {@code null} context. It means that notification is disabled by default.
     *
     * @param code name of notification.
     */
    public NotificationDescriptor(String code) {
        this(code, null);
    }

    /**
     * Creates new notification with descriptor.
     *
     * @param code  name of notification.
     * @param scope scope of notification or {@code null} is notification is disabled by default.
     */
    public NotificationDescriptor(String code, NotificationScope scope) {
        this.code = code;
        this.scope = scope;
    }

    /**
     * Returns unique code of the notification.
     *
     * @return the unique code of the notification.
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns section for this descriptor. TournamentSection is part of {@code code} til last dot.
     *
     * @return the section for this descriptor
     */
    public String getSection() {
        return code.substring(0, code.lastIndexOf("."));
    }

    /**
     * Returns scope for this notification.
     *
     * @return the notification scope.
     */
    public NotificationScope getScope() {
        return scope;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("NotificationDescriptor");
        sb.append("{code='").append(code).append('\'');
        sb.append(", scope=").append(scope);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(NotificationDescriptor o) {
        return code.compareTo(o.code);
    }
}