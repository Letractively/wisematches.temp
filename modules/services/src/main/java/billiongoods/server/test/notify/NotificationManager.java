package billiongoods.server.test.notify;

import billiongoods.core.Member;

import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface NotificationManager {
	/**
	 * Returns unmodifiable collection of all known notification codes.
	 *
	 * @return unmodifiable collection of all known notification codes.
	 */
	Set<String> getNotificationCodes();

	/**
	 * Returns default descriptor for specified notification.
	 *
	 * @param code notification code
	 * @return default descriptor for specified notification or {@code null} if there is no notification with specified code.
	 */
	NotificationDescriptor getDescriptor(String code);

	/**
	 * Returns personal descriptor for specified player and notification code.
	 *
	 * @param code   notification code
	 * @param player personality who's descriptor should be returned.
	 * @return personal descriptor for notification of default descriptor if personality doesn't have personal
	 * descriptor or {@code null} if there is no notification with specified code.
	 */
	NotificationScope getNotificationScope(String code, Member player);

	/**
	 * Changes personal descriptor for notification.
	 *
	 * @param code   notification code
	 * @param player player who's descriptor should be updated.
	 * @param scope  personal descriptor for notification.
	 * @return previous personal descriptor or {@code null} if player don't have previous personal descriptor
	 */
	NotificationScope setNotificationScope(String code, Member player, NotificationScope scope);
}
