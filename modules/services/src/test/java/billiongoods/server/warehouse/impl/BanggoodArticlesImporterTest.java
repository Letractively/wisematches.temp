package billiongoods.server.warehouse.impl;

import billiongoods.server.services.price.ExchangeManager;
import billiongoods.server.services.price.MarkupCalculator;
import billiongoods.server.warehouse.*;
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
		final ExchangeManager exchangeManager = createMock(ExchangeManager.class);
		expect(exchangeManager.getMarkupCalculator()).andReturn(new MarkupCalculator(0.3d, 0.2d));
		replay(exchangeManager);

		final Category category = createMock(Category.class);
		replay(category);

		final Article article = createMock(Article.class);
		expect(article.getId()).andReturn(13);
		replay(article);

		final ArticleManager articleManager = createMock(ArticleManager.class);
		expect(articleManager.createArticle("WLtoys V911-1 RC Helicopter Spare Parts Green Main Blade V911-1-2", "<br /><span style=\"font-size:12px;\"><span style=\"font-family: arial-helvetica-sans-serif;\"><strong>WLtoys V911-1 RC Helicopter Spare Parts Green Main Blade V911-1-2</strong><br /><br /><strong>Description:</strong><br /><br />Brand: WLtoys<br />Item Name: Main Blade<br />NO.: V911-1-2<br />Usage:For WLtoys V911-1 RC Helicopter<br /><br /><strong>Package Included:</strong><br />1 x Main Blade</span></span>", category, new Price(1.84d, null), 0.05d, null, null, null, null, null, "82283", "SKU088161", Supplier.BANGGOOD, new Price(1.28d, null))).andReturn(article);
		replay(articleManager);

		final BanggoodArticlesImporter importer = new BanggoodArticlesImporter();
		importer.setExchangeManager(exchangeManager);
		importer.setArticleManager(articleManager);

		importer.importArticles(category, getClass().getResourceAsStream("/banggood_packer.csv"));

		verify(articleManager);
	}
}
