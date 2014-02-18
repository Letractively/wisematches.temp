package billiongoods.server.services.notify.impl;

import billiongoods.core.task.executor.TransactionAwareExecutor;
import billiongoods.server.services.address.Address;
import billiongoods.server.services.notify.Notification;
import billiongoods.server.services.notify.NotificationService;
import billiongoods.server.services.notify.impl.center.NotificationOriginCenter;
import billiongoods.server.services.payment.*;
import billiongoods.server.services.payment.impl.HibernateOrder;
import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMock;
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
public class MailSelectorOriginCenterTest {
	private final Capture<Notification> publishedNotifications = new Capture<>(CaptureType.ALL);
	@Autowired
	private NotificationService notificationService;

	@Autowired
	private NotificationOriginCenter publisherCenter;

	@Autowired
	private PlatformTransactionManager transactionManager;

	public MailSelectorOriginCenterTest() {
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
	}

	@After
	public void tearDown() throws Exception {
		for (Notification notification : publishedNotifications.getValues()) {
			System.out.println(notification);
		}
	}

	@Test
	public void testOrderNotifications() {
		final Capture<OrderListener> listenerCapture = new Capture<>();

		final OrderManager orderManager = EasyMock.createMock(OrderManager.class);
		orderManager.addOrderListener(capture(listenerCapture));
		orderManager.removeOrderListener(isA(OrderListener.class));
		replay(orderManager);

		publisherCenter.setOrderManager(orderManager);

		final Address address = new Address("asd", "bsdf", "+7-123-123-122", "124434", "asd", "wqe", "asd");

		final Order order = new HibernateOrder(123L, 10d, 1d, null, new Shipment(12d, address, ShipmentType.REGISTERED), true);

		final OrderListener listener = listenerCapture.getValue();
		listener.orderStateChanged(order, null, OrderState.NEW);

		publisherCenter.setOrderManager(null);
	}
}