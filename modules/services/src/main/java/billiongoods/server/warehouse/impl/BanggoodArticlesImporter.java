package billiongoods.server.warehouse.impl;

import au.com.bytecode.opencsv.CSVReader;
import billiongoods.server.warehouse.ArticleManager;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Supplier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodArticlesImporter {
    private ArticleManager articleManager;

    public BanggoodArticlesImporter() {
    }

    public void importArticles(Category category, InputStream stream) throws IOException {
        final CSVReader reader = new CSVReader(new InputStreamReader(stream));

        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            final String sku = nextLine[0];
            final String name = nextLine[1];
            final double price = Float.parseFloat(nextLine[3]);
            final double weight = Float.parseFloat(nextLine[4]);
            final String desc = nextLine[5];

            String id = nextLine[6];
            id = id.substring(id.lastIndexOf("-") + 1, id.lastIndexOf("."));

            final double price1 = Math.round((price + price * 0.15f + 0.30f) * 100f) / 100.0f;

            articleManager.createArticle(name, desc, category, price1, null, weight, null, null, null, null, null, null, id, sku, Supplier.BANGGOOD, price, null);
        }
    }

    public void setArticleManager(ArticleManager articleManager) {
        this.articleManager = articleManager;
    }
}
