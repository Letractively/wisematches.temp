package billiongoods.server.services.coupon.impl;

import billiongoods.server.services.coupon.Coupon;
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
public class HibernateCouponManagerTest {
	@Autowired
	private SessionFactory sessionFactory;

	public HibernateCouponManagerTest() {
	}

	@Test
	public void test() {
		final HibernateCouponManager manager = new HibernateCouponManager();
		manager.setSessionFactory(sessionFactory);

		final Coupon coupon = manager.getCoupon("mock");
	}
}
