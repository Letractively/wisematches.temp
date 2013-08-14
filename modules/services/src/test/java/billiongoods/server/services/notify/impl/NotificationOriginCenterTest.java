package billiongoods.server.services.notify.impl;

import billiongoods.core.Member;
import billiongoods.core.task.executor.TransactionAwareExecutor;
import billiongoods.server.services.notify.Notification;
import billiongoods.server.services.notify.NotificationService;
import billiongoods.server.services.notify.impl.center.NotificationOriginCenter;
import org.easymock.Capture;
import org.easymock.CaptureType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.fail;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/properties-config.xml",
		"classpath:/config/database-config.xml",
		"classpath:/config/personality-config.xml",
		"classpath:/config/billiongoods-config.xml",
})
public class NotificationOriginCenterTest {
	private final Capture<Notification> publishedNotifications = new Capture<>(CaptureType.ALL);
	@Autowired
	private NotificationService notificationService;
	@Autowired
	private NotificationOriginCenter publisherCenter;
	@Autowired
	private PlatformTransactionManager transactionManager;
	private Member p1;
	private Member p2;

	public NotificationOriginCenterTest() {
	}

	@Before
	public void setUp() throws Exception {
		final TransactionAwareExecutor taskExecutor = new TransactionAwareExecutor();
		taskExecutor.setTaskExecutor(new SyncTaskExecutor());
		taskExecutor.setTransactionManager(transactionManager);

		final NotificationPublisher notificationPublisher = createMock(NotificationPublisher.class);
		notificationPublisher.publishNotification(capture(publishedNotifications));
		expectLastCall().anyTimes();
		replay(notificationPublisher);

		if (notificationService instanceof DistributedNotificationService) {
			final DistributedNotificationService service = (DistributedNotificationService) notificationService;
			service.setTaskExecutor(taskExecutor);
			service.setNotificationPublishers(Arrays.asList(notificationPublisher));
		} else {
			fail("NotificationService is not DistributedNotificationService");
		}

		publisherCenter.setTaskExecutor(new SyncTaskExecutor());
	}

	@After
	public void tearDown() throws Exception {
		for (Notification notification : publishedNotifications.getValues()) {
			System.out.println(notification);
		}
	}

	@Test
	public void empty() {
	}
}