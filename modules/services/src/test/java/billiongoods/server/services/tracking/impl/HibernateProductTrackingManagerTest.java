package billiongoods.server.services.tracking.impl;

import billiongoods.server.services.tracking.TrackingContext;
import billiongoods.server.services.tracking.TrackingType;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/properties-config.xml",
		"classpath:/config/database-config.xml"
})
public class HibernateProductTrackingManagerTest {
	@Autowired
	private SessionFactory sessionFactory;

	public HibernateProductTrackingManagerTest() {
	}

	@Test
	public void test() {
		final HibernateProductTrackingManager manager = new HibernateProductTrackingManager();
		manager.setSessionFactory(sessionFactory);

		final TrackingContext context = new TrackingContext(null, TrackingType.AVAILABILITY, null);
		int tc1 = manager.getTotalCount(context);
	}
}
