package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.AttributeManager;
import billiongoods.server.warehouse.Catalog;
import billiongoods.server.warehouse.Category;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

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
public class HibernateCategoryManagerTest {
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private AttributeManager attributeManager;

	public HibernateCategoryManagerTest() {
	}

	@Test
	public void test() throws Exception {
		final HibernateCategoryManager manager = new HibernateCategoryManager();
		manager.setSessionFactory(sessionFactory);
		manager.setAttributeManager(attributeManager);
		manager.afterPropertiesSet();

		final Catalog catalog = manager.getCatalog();
		assertNotNull(catalog);

		final Category category = manager.createCategory("mockC", "mockD", null, null, 0);
		assertNotNull(category);

		assertSame(category, manager.getCategory(category.getId()));

		dumpCategory(catalog.getRootCategories(), "");
	}

	private void dumpCategory(List<Category> catalog, String s) {
		for (Category category : catalog) {
			System.out.println(s + category);

			dumpCategory(category.getChildren(), s + "   ");
		}
	}
}
