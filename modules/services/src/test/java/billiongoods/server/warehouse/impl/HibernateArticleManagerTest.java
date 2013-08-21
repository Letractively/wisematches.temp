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
		"classpath:/config/billiongoods-config.xml"
})
public class HibernateArticleManagerTest {
	@Autowired
	private ArticleManager articleManager;

	@Autowired
	private CategoryManager categoryManager;

	public HibernateArticleManagerTest() {
	}

	@Test
	public void test() {
		final int totalCount = articleManager.getTotalCount(null);
		assertTrue(totalCount > 0);

		final List<ArticleDescription> descriptions = articleManager.searchEntities(null, null, Range.FIRST);
		assertEquals(1, descriptions.size());

		final ArticleDescription description = descriptions.get(0);

		final DefaultCategory category = new DefaultCategory(new HibernateCategory("asdf", "test", null, 0, null), null);

		final List<ArticleDescription> ctxDescriptions1 = articleManager.searchEntities(new ArticleContext(category), null, Range.FIRST);
		assertEquals(0, ctxDescriptions1.size());

		final Category category1 = categoryManager.getCategory(description.getCategoryId());

		final List<ArticleDescription> ctxDescriptions2 = articleManager.searchEntities(new ArticleContext(category1), null, Range.FIRST);
		assertEquals(1, ctxDescriptions2.size());

		final Article article = articleManager.getArticle(description.getId());

		final List<Option> options = article.getOptions();
		System.out.println("Options: " + options);

		System.out.println("==========");
		System.out.println(article);
		assertNotNull(article);
		assertNotNull(article.getSupplierInfo());
	}
}
