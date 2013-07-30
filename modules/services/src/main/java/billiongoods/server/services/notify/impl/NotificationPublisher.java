package billiongoods.server.services.notify.impl;

import billiongoods.server.services.notify.Notification;
import billiongoods.server.services.notify.NotificationException;
import billiongoods.server.services.notify.NotificationScope;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface NotificationPublisher {
    String getName();

    NotificationScope getNotificationScope();


    void publishNotification(Notification notification) throws NotificationException;
}
