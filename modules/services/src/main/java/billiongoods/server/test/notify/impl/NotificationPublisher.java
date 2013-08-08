package billiongoods.server.test.notify.impl;

import billiongoods.server.test.notify.Notification;
import billiongoods.server.test.notify.NotificationException;
import billiongoods.server.test.notify.NotificationScope;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface NotificationPublisher {
	String getName();

	NotificationScope getNotificationScope();


	void publishNotification(Notification notification) throws NotificationException;
}
