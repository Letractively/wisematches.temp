package billiongoods.server.services.catalog.impl;

import billiongoods.server.services.catalog.CatalogItem;
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
        "classpath:/config/database-config.xml"
})
public class HibernateCatalogManagerTest {
    @Autowired
    private SessionFactory sessionFactory;

    public HibernateCatalogManagerTest() {
    }

    @Test
    public void test() throws Exception {
        final HibernateCatalogManager manager = new HibernateCatalogManager();
        manager.setSessionFactory(sessionFactory);
        manager.afterPropertiesSet();

        final CatalogItem catalog = manager.getCatalog();
        assertNotNull(catalog);

        final int size = catalog.getCatalogItems().size();

        final CatalogItem t1 = manager.addCatalogItem("Test1", null);
        final CatalogItem t2 = manager.addCatalogItem("Test2", null);

        final CatalogItem c1 = manager.addCatalogItem("Child1", t1);
        final CatalogItem c2 = manager.addCatalogItem("Child2", t1);

        assertEquals(size + 2, catalog.getCatalogItems().size());
        assertSame(t1, c1.getParent());
        assertSame(t1, c2.getParent());

        manager.removeCatalogItem(t1, t2);
        assertEquals(size + 1, catalog.getCatalogItems().size());
        assertSame(t2, c1.getParent());
        assertSame(t2, c2.getParent());

        assertEquals(0, t1.getCatalogItems().size());
    }
}
