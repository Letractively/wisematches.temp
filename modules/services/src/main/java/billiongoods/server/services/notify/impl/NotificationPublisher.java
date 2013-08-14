package billiongoods.server.services.notify.impl;

import billiongoods.server.services.notify.Notification;
import billiongoods.server.services.notify.NotificationException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface NotificationPublisher {
	String getName();

	void publishNotification(Notification notification) throws NotificationException;
}
