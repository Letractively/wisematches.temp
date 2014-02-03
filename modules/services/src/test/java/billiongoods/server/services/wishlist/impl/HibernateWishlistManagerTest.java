package billiongoods.server.services.wishlist.impl;

import billiongoods.core.Personality;
import billiongoods.core.Visitor;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/properties-config.xml",
		"classpath:/config/database-config.xml"
})
public class HibernateWishlistManagerTest {
	@Autowired
	private SessionFactory sessionFactory;

	public HibernateWishlistManagerTest() {
	}

	@Test
	public void testGlobal() throws Exception {
		final HibernateWishlistManager manager = new HibernateWishlistManager();
		manager.setSessionFactory(sessionFactory);

		final Personality p1 = new Visitor(System.nanoTime());
		final Personality p2 = new Visitor(System.nanoTime());

		manager.addWishProducts(p1, 1, 2, 3);
		manager.addWishProducts(p2, 2, 3, 4, 5);
		assertEquals(3, manager.getTotalCount(p1));
		assertEquals(4, manager.getTotalCount(p2));
		assertEquals(Arrays.asList(1, 2, 3), manager.searchEntities(p1, null, null, null));
		assertEquals(Arrays.asList(2, 3, 4, 5), manager.searchEntities(p2, null, null, null));

		manager.removeWishProducts(p1, 2);
		assertEquals(2, manager.getTotalCount(p1));
		assertEquals(4, manager.getTotalCount(p2));
		assertEquals(Arrays.asList(1, 3), manager.searchEntities(p1, null, null, null));
		assertEquals(Arrays.asList(2, 3, 4, 5), manager.searchEntities(p2, null, null, null));

		manager.removeWishProducts(p1, 2);
		assertEquals(2, manager.getTotalCount(p1));
		assertEquals(4, manager.getTotalCount(p2));
		assertEquals(Arrays.asList(1, 3), manager.searchEntities(p1, null, null, null));
		assertEquals(Arrays.asList(2, 3, 4, 5), manager.searchEntities(p2, null, null, null));

		manager.removeWishProducts(p2, 3, 4);
		assertEquals(2, manager.getTotalCount(p1));
		assertEquals(2, manager.getTotalCount(p2));
		assertEquals(Arrays.asList(1, 3), manager.searchEntities(p1, null, null, null));
		assertEquals(Arrays.asList(2, 5), manager.searchEntities(p2, null, null, null));
	}
}
