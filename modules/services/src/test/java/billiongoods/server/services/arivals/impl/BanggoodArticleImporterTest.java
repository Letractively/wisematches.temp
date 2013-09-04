package billiongoods.server.services.arivals.impl;

import billiongoods.server.services.price.ExchangeManager;
import billiongoods.server.services.price.MarkupCalculator;
import billiongoods.server.warehouse.*;
import org.junit.Test;

import java.io.IOException;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodArticleImporterTest {
	public BanggoodArticleImporterTest() {
	}

	@Test
	public void testSpanCleaner() {
		final BanggoodArticleImporter importer = new BanggoodArticleImporter();
		String s = importer.cleanSpan("<br /><span style=\"font-size:12px;\"><span style=\"font-family: arial-helvetica-sans-serif;\"><strong>WLtoys V911-1 RC Helicopter Spare Parts Green Main Blade V911-1-2</strong><br /><br /><strong>Description:</strong><br /><br />Brand: WLtoys<br />Item Name: Main Blade<br />NO.: V911-1-2<br />Usage:For WLtoys V911-1 RC Helicopter<br /><br /><strong>Package Included:</strong><br />1 x Main Blade</span></span>");
		assertEquals("<br /><strong>WLtoys V911-1 RC Helicopter Spare Parts Green Main Blade V911-1-2</strong><br /><br /><strong>Description:</strong><br /><br />Brand: WLtoys<br />Item Name: Main Blade<br />NO.: V911-1-2<br />Usage:For WLtoys V911-1 RC Helicopter<br /><br /><strong>Package Included:</strong><br />1 x Main Blade", s);
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

		final ArticleEditor editor = new ArticleEditor();
		editor.setName("WLtoys V911-1 RC Helicopter Spare Parts Green Main Blade V911-1-2");
		editor.setDescription("<br /><span style=\"font-size:12px;\"><span style=\"font-family: arial-helvetica-sans-serif;\"><strong>WLtoys V911-1 RC Helicopter Spare Parts Green Main Blade V911-1-2</strong><br /><br /><strong>Description:</strong><br /><br />Brand: WLtoys<br />Item Name: Main Blade<br />NO.: V911-1-2<br />Usage:For WLtoys V911-1 RC Helicopter<br /><br /><strong>Package Included:</strong><br />1 x Main Blade</span></span>");
		editor.setCategoryId(13);
		editor.setPrice(new Price(1.84d, null));
		editor.setWeight(0.05d);
		editor.setReferenceUri("82283");
		editor.setReferenceCode("SKU088161");
		editor.setWholesaler(Supplier.BANGGOOD);
		editor.setSupplierPrice(new Price(1.28d, null));

		final ArticleManager articleManager = createMock(ArticleManager.class);
		expect(articleManager.createArticle(editor)).andReturn(article);
		replay(articleManager);

		final BanggoodArticleImporter importer = new BanggoodArticleImporter();
		importer.setExchangeManager(exchangeManager);
		importer.setArticleManager(articleManager);

//		importer.importArticles(category, getClass().getResourceAsStream("/banggood_packer.csv"));

		verify(articleManager);
	}
}
