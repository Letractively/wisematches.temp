package billiongoods.server.test.notify.impl;


import billiongoods.core.Member;
import billiongoods.core.Personality;
import billiongoods.core.task.BreakingDayListener;
import billiongoods.server.test.notify.NotificationException;
import billiongoods.server.test.notify.NotificationSender;
import billiongoods.server.test.notify.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.TaskExecutor;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class NotificationOriginCenter implements BreakingDayListener, InitializingBean {
	private TaskExecutor taskExecutor;

	private NotificationService notificationDistributor;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.notification.OriginCenter");

	public NotificationOriginCenter() {
	}

	protected void processNotification(long person, String code, Object context) {
/*
		final Member member = personalityManager.getMember(person);
		if (member != null) {
			fireNotification(code, member, context);
		}
*/
	}

	protected void processNotification(Personality person, String code, Object context) {
		if (person instanceof Member) {
			fireNotification(code, (Member) person, context);
		}
	}

	private void fireNotification(String code, Member player, Object context) {
		try {
			notificationDistributor.raiseNotification(code, player, NotificationSender.GAME, context);
			log.info("Notification was raised to {} [{}]", player, code);
		} catch (NotificationException ex) {
			log.error("Notification can't be sent to player: code=" + code + ", player=" + player.getId(), ex);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Override
	public void breakingDayTime(Date midnight) {
	}

	public void setTaskExecutor(TaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setNotificationService(NotificationService notificationDistributor) {
		this.notificationDistributor = notificationDistributor;
	}
}
