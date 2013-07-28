package billiongoods.server.warehouse.impl;

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

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/config/properties-config.xml",
        "classpath:/config/database-config.xml"
})
public class HibernateCategoryManagerTest {
    @Autowired
    private SessionFactory sessionFactory;

    public HibernateCategoryManagerTest() {
    }

    @Test
    public void test() throws Exception {
        final HibernateCategoryManager manager = new HibernateCategoryManager();
        manager.setSessionFactory(sessionFactory);
        manager.afterPropertiesSet();

        final Catalog catalog = manager.getCatalog();
        assertNotNull(catalog);

        dumpCategory(catalog.getRootCategories(), "");

//		final int size = catalog.getChildren().size();
//		System.out.println(size);

/*
        final Category t1 = manager.addCategory("Test1", null);
		final Category t2 = manager.addCategory("Test2", null);

		final Category c1 = manager.addCategory("Child1", t1);
		final Category c2 = manager.addCategory("Child2", t1);

		assertEquals(size + 2, catalog.getChildren().size());
		assertSame(t1, c1.getParent());
		assertSame(t1, c2.getParent());

		manager.removeCategory(t1, t2);
		assertEquals(size + 1, catalog.getChildren().size());
		assertSame(t2, c1.getParent());
		assertSame(t2, c2.getParent());

		assertEquals(0, t1.getChildren().size());
*/
    }

    private void dumpCategory(List<Category> catalog, String s) {
        for (Category category : catalog) {
            System.out.println(s + category);

            dumpCategory(category.getChildren(), s + "   ");
        }
    }
}
