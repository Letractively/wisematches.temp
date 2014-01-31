package billiongoods.server.services.notify.impl;


import billiongoods.core.task.TransactionalExecutor;
import billiongoods.server.services.notify.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DistributedNotificationService implements NotificationService {
	private TransactionalExecutor taskExecutor;
	private NotificationConverter notificationConverter;

	private final Collection<NotificationPublisher> publishers = new ArrayList<>();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.notification.PublishService");

	public DistributedNotificationService() {
	}

	public Notification raiseNotification(Recipient recipient, Sender sender, String code, Object context, Object... args) throws NotificationException {
		final Notification notification = notificationConverter.createNotification(recipient, sender, code, context, args);

		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				for (final NotificationPublisher publisher : publishers) {
					try {
						publisher.publishNotification(notification);
					} catch (NotificationException ex) {
						log.error("Notification can't be processed: code={},publisher={}", notification.getCode(), publisher.getName(), ex);
					}
				}
			}
		});
		return notification;
	}

	public void setTaskExecutor(TransactionalExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setNotificationConverter(NotificationConverter notificationConverter) {
		this.notificationConverter = notificationConverter;
	}

	public void setNotificationPublishers(Collection<NotificationPublisher> publishers) {
		this.publishers.clear();

		if (publishers != null) {
			this.publishers.addAll(publishers);
		}
	}
}
