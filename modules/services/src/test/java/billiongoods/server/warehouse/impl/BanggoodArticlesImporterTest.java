package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.ArticleManager;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Supplier;
import org.junit.Test;

import java.io.IOException;

import static org.easymock.EasyMock.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodArticlesImporterTest {
	public BanggoodArticlesImporterTest() {
	}

	@Test
	public void test() throws IOException {
		final Category category = createMock(Category.class);

		final ArticleManager articleManager = createMock(ArticleManager.class);
		expect(articleManager.createArticle("WLtoys V911-1 RC Helicopter Spare Parts Green Main Blade V911-1-2", "<br /><span style=\"font-size:12px;\"><span style=\"font-family: arial-helvetica-sans-serif;\"><strong>WLtoys V911-1 RC Helicopter Spare Parts Green Main Blade V911-1-2</strong><br /><br /><strong>Description:</strong><br /><br />Brand: WLtoys<br />Item Name: Main Blade<br />NO.: V911-1-2<br />Usage:For WLtoys V911-1 RC Helicopter<br /><br /><strong>Package Included:</strong><br />1 x Main Blade</span></span>", category, 1.77f, null, 0.12f, null, null, null, null, null, "82283", "SKU088161", Supplier.BANGGOOD, 1.28f, null)).andReturn(null);
		replay(articleManager);

		final BanggoodArticlesImporter importer = new BanggoodArticlesImporter();
		importer.setArticleManager(articleManager);

		importer.importArticles(category, getClass().getResourceAsStream("/banggood_packer.csv"));

		verify(articleManager);
	}
}
