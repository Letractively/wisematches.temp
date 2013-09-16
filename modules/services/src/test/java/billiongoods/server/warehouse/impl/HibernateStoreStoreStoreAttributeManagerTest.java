package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.StoreAttribute;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/properties-config.xml",
		"classpath:/config/database-config.xml",
		"classpath:/config/personality-config.xml",
		"classpath:/config/billiongoods-config.xml"
})
public class HibernateStoreStoreStoreAttributeManagerTest {
	@Autowired
	private SessionFactory sessionFactory;

	public HibernateStoreStoreStoreAttributeManagerTest() {
	}

	@Test
	public void test() throws Exception {
		final HibernateStoreAttributeManager manager = new HibernateStoreAttributeManager();
		manager.setSessionFactory(sessionFactory);
		manager.afterPropertiesSet();

		final StoreAttribute attr1 = manager.createAttribute("mock", "muck");
		assertNotNull(attr1);

		assertSame(attr1, manager.getAttribute(attr1.getId()));
		assertTrue(manager.getAttributes().contains(attr1));

		final StoreAttribute attr2 = manager.getAttribute(attr1.getId());
		assertNotNull(attr2);
		assertEquals(attr1.getName(), attr2.getName());
		assertEquals(attr1.getUnit(), attr2.getUnit());

		assertTrue(manager.getAttributes().contains(attr1));
		assertTrue(manager.getAttributes().contains(attr2));
	}
}
