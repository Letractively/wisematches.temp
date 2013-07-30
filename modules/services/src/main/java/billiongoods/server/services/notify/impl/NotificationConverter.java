package billiongoods.server.services.notify.impl;

import billiongoods.core.Member;
import billiongoods.server.services.notify.Notification;
import billiongoods.server.services.notify.NotificationSender;
import billiongoods.server.services.notify.TransformationException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface NotificationConverter {
    Notification createNotification(String code, Member target, NotificationSender sender, Object context) throws TransformationException;
}
