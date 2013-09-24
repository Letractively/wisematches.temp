package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Attribute;
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

		final Category category = manager.createCategory(new Category.Editor("mockC", "mockD", null, 0));
		assertNotNull(category);
		assertEquals(0, category.getParameters().size());

		assertSame(category, manager.getCategory(category.getId()));

		final Attribute next = attributeManager.getAttributes().iterator().next();
		manager.addParameter(category, next);

		manager.addParameterValue(category, next, "V1");
		assertEquals(1, category.getParameters().size());
		assertEquals(1, category.getParameters().iterator().next().getValues().size());

		manager.addParameterValue(category, next, "V2");
		assertEquals(1, category.getParameters().size());
		assertEquals(2, category.getParameters().iterator().next().getValues().size());

		manager.removeParameterValue(category, next, "V1");
		assertEquals(1, category.getParameters().size());
		assertEquals(1, category.getParameters().iterator().next().getValues().size());

		manager.removeParameter(category, next);
		assertEquals(0, category.getParameters().size());

		dumpCategory(catalog.getRootCategories(), "");
	}

	private void dumpCategory(List<Category> catalog, String s) {
		for (Category category : catalog) {
			System.out.println(s + category);

			dumpCategory(category.getChildren(), s + "   ");
		}
	}
}
