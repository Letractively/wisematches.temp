package billiongoods.server.services.notify.impl;

import billiongoods.server.services.notify.Notification;
import billiongoods.server.services.notify.Recipient;
import billiongoods.server.services.notify.Sender;
import billiongoods.server.services.notify.TransformationException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface NotificationConverter {
	Notification createNotification(String code, Recipient recipient, Sender sender, Object context) throws TransformationException;
}
