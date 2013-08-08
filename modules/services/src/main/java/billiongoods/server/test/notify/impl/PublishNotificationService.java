package billiongoods.server.test.notify.impl;


import billiongoods.core.Member;
import billiongoods.core.task.TransactionalExecutor;
import billiongoods.server.test.notify.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PublishNotificationService implements NotificationService {
	private TransactionalExecutor taskExecutor;
	private NotificationManager notificationManager;
	private NotificationConverter notificationConverter;

	private final Collection<NotificationPublisher> publishers = new ArrayList<>();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.notification.PublishService");

	public PublishNotificationService() {
	}

	@Override
	public Notification raiseNotification(String code, Member target, NotificationSender sender, Object context) throws NotificationException {
		// TODO: commented
/*
		final NotificationScope notificationScope = notificationManager.getNotificationScope(code, target);
        final NotificationScope personalScope = notificationScope != null ? notificationScope : NotificationScope.EXTERNAL;

        final Notification notification = notificationConverter.createNotification(code, target, sender, context);

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                for (final NotificationPublisher publisher : publishers) {
                    final NotificationScope publisherScope = publisher.getNotificationScope();
                    if ((publisherScope.isInternal() && personalScope.isInternal()) ||
                            (publisherScope.isExternal() && personalScope.isExternal())) {
                        try {
                            publisher.publishNotification(notification);
                        } catch (NotificationException ex) {
                            log.error("Notification can't be processed: code={},publisher={}", notification.getCode(), publisher.getName(), ex);
                        }
                    }
                }
            }
        });
        return notification;
*/
		throw new UnsupportedOperationException("commented");
	}

	public void setTaskExecutor(TransactionalExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setNotificationManager(NotificationManager notificationManager) {
		this.notificationManager = notificationManager;
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
