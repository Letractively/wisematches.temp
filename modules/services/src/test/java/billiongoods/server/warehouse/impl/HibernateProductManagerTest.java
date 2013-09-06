package billiongoods.server.warehouse.impl;

import billiongoods.core.search.Range;
import billiongoods.server.warehouse.*;
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
public class HibernateProductManagerTest {
	@Autowired
	private ProductManager productManager;

	@Autowired
	private CategoryManager categoryManager;

	public HibernateProductManagerTest() {
	}

	@Test
	public void test() {
		final int totalCount = productManager.getTotalCount(null);
		assertTrue(totalCount > 0);

		final List<ProductDescription> descriptions = productManager.searchEntities(null, null, Range.FIRST);
		assertEquals(1, descriptions.size());

		final ProductDescription description = descriptions.get(0);

		final DefaultCategory category = new DefaultCategory(new HibernateCategory("asdf", "test", null, 0, null), null);

		final List<ProductDescription> ctxDescriptions1 = productManager.searchEntities(new ProductContext(category), null, Range.FIRST);
		assertEquals(0, ctxDescriptions1.size());

		final Category category1 = categoryManager.getCategory(description.getCategoryId());

		final List<ProductDescription> ctxDescriptions2 = productManager.searchEntities(new ProductContext(category1), null, Range.FIRST);
		assertEquals(1, ctxDescriptions2.size());

		final Product product = productManager.getProduct(description.getId());

		final List<Option> options = product.getOptions();
		System.out.println("Options: " + options);

		productManager.updateSold(product.getId(), 10);

		productManager.updatePrice(product.getId(), new Price(2.3d, null), new Price(3.d, null));
		productManager.updatePrice(product.getId(), new Price(12.3d, 54.d), new Price(43.d, 765.d));

		System.out.println("==========");
		System.out.println(product);
		assertNotNull(product);
		assertNotNull(product.getSupplierInfo());
	}
}
