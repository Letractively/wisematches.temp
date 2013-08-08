package billiongoods.server.test.notify.impl;

import billiongoods.core.Member;
import billiongoods.server.test.notify.Notification;
import billiongoods.server.test.notify.NotificationSender;
import billiongoods.server.test.notify.TransformationException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface NotificationConverter {
	Notification createNotification(String code, Member target, NotificationSender sender, Object context) throws TransformationException;
}
