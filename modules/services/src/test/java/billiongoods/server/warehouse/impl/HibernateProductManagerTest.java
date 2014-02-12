package billiongoods.server.warehouse.impl;

import billiongoods.core.search.Range;
import billiongoods.server.warehouse.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
		assertTrue(productManager.getTotalCount(null) > 0);

		final List<ProductPreview> descriptions = productManager.searchEntities(null, null, Range.FIRST, null);
		assertEquals(1, descriptions.size());

		final ProductPreview description = descriptions.get(0);

		final DefaultCategory category = new DefaultCategory(new HibernateCategory("asdf", "asdf_s", "test", null, 0), null);

		final List<ProductPreview> ctxDescriptions1 = productManager.searchEntities(new ProductContext(category), null, Range.FIRST, null);
		assertEquals(0, ctxDescriptions1.size());

		final Category category1 = categoryManager.getCategory(description.getCategoryId());

		final List<ProductPreview> ctxDescriptions2 = productManager.searchEntities(new ProductContext(category1), null, Range.FIRST, null);
		assertEquals(1, ctxDescriptions2.size());

		final Product product = productManager.getProduct(description.getId());

		final List<Option> options = product.getOptions();
		System.out.println("Options: " + options);

		productManager.updateSold(product.getId(), 10);

		productManager.updateProductInformation(product.getId(), new Price(2.3d, null), new Price(3.d, null), new StockInfo(0, null));
		productManager.updateProductInformation(product.getId(), new Price(12.3d, 54.d), new Price(43.d, 765.d), new StockInfo(null, new Date()));

		final SupplierInfo supplierInfo = productManager.getSupplierInfo(description.getId());
		assertNotNull(supplierInfo);

		System.out.println("==========");
		System.out.println(product);
		assertNotNull(product);
		assertNotNull(product.getSupplierInfo());
	}

	@Test
	public void testFilteringAbility() {
		final Filtering filteringAbility = productManager.getFilteringAbility(null, null);
		assertNotNull(filteringAbility);

		System.out.println(filteringAbility);
	}
}
