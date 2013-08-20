package billiongoods.server.warehouse.impl;

import au.com.bytecode.opencsv.CSVReader;
import billiongoods.server.services.price.ExchangeManager;
import billiongoods.server.warehouse.Article;
import billiongoods.server.warehouse.ArticleManager;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodArticlesImporter {
	private ArticleManager articleManager;
	private ExchangeManager exchangeManager;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.BanggoodArticlesImporter");

	public BanggoodArticlesImporter() {
	}

	public void importArticles(Category category, InputStream stream) throws IOException {
		final CSVReader reader = new CSVReader(new InputStreamReader(stream));

		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			final String sku = nextLine[0];
			final String name = nextLine[1];
			final double price = Double.parseDouble(nextLine[3]);
			final double weight = Double.parseDouble(nextLine[4]);
			final String desc = nextLine[5];

			String id = nextLine[6];
			id = id.substring(id.lastIndexOf("-") + 1, id.lastIndexOf("."));

			final double price1 = exchangeManager.getMarkupCalculator().calculateFinalPrice(price);

			final Article article = articleManager.createArticle(name, desc, category, price1, null, weight, null, null, null, null, null, id, sku, Supplier.BANGGOOD, price, null);

			log.info("Article imported: {}", article.getId());
		}
	}

	public void setArticleManager(ArticleManager articleManager) {
		this.articleManager = articleManager;
	}

	public void setExchangeManager(ExchangeManager exchangeManager) {
		this.exchangeManager = exchangeManager;
	}
}
