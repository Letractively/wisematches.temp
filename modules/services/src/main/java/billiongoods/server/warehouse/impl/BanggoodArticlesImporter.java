package billiongoods.server.warehouse.impl;

import au.com.bytecode.opencsv.CSVReader;
import billiongoods.server.services.image.ImageManager;
import billiongoods.server.services.price.ExchangeManager;
import billiongoods.server.warehouse.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodArticlesImporter {
    private ImageManager imageManager;
    private ArticleManager articleManager;
    private ExchangeManager exchangeManager;


    private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.BanggoodArticlesImporter");

    public BanggoodArticlesImporter() {
    }

    public void importArticles(Category category, InputStream descStream, InputStream imagesStream) throws IOException {
        final Map<String, Set<String>> images = parseImagesLinks(imagesStream);

        final CSVReader reader = new CSVReader(new InputStreamReader(descStream));
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            final String sku = nextLine[0];
            final String name = nextLine[1];
            final Price supplierPrice = new Price(Double.parseDouble(nextLine[3]), null);
            final double weight = Double.parseDouble(nextLine[4]);
            final String desc = nextLine[5];

            String id = nextLine[6];
            id = id.substring(id.lastIndexOf("-") + 1, id.lastIndexOf("."));

            log.info("Importing article for SKU: " + sku);

            final Article article1 = articleManager.getArticle(sku);
            if (article1 != null) {
                log.info("Article with the same SKU already exist. Moving on.");
                continue;
            }

            final Price price = exchangeManager.getMarkupCalculator().calculateMarkupPrice(supplierPrice);
            final Article article = articleManager.createArticle(name, desc, category, price, weight, null, null, null, null, null, id, sku, Supplier.BANGGOOD, supplierPrice);

            final Set<String> urls = images.get(sku);
            if (urls != null) {
                int index = 1;
                final List<String> codes = new ArrayList<>(urls.size());
                for (String url : urls) {
                    final String code = String.valueOf(index++);
                    log.info("Importing [{} of {}] image {} from {}", index - 1, urls.size(), code, url);
                    try (InputStream inputStream = new URL(url).openStream()) {
                        imageManager.addImage(article, code, inputStream);
                        codes.add(code);
                    } catch (Exception ex) {
                        log.error("SKU image can't be imported", ex);
                    }
                }

                if (!codes.isEmpty()) {
                    articleManager.updateArticle(article.getId(), name, desc, category, price, weight, null,
                            codes.iterator().next(), codes, null, null, id, sku, Supplier.BANGGOOD, supplierPrice);
                }
            }
            log.info("Article imported: {}", article.getId());
        }
        log.info("All articles imported");
    }

    private Map<String, Set<String>> parseImagesLinks(InputStream stream) throws IOException {
        final Map<String, Set<String>> res = new HashMap<>();

        String[] nextLine;
        final CSVReader reader = new CSVReader(new InputStreamReader(stream));
        while ((nextLine = reader.readNext()) != null) {
            final String sku = nextLine[0];
            final String link = nextLine[1];

            Set<String> strings = res.get(sku);
            if (strings == null) {
                strings = new HashSet<>();
                res.put(sku, strings);
            }
            strings.add(link);
        }
        return res;
    }

    public void setImageManager(ImageManager imageManager) {
        this.imageManager = imageManager;
    }

    public void setArticleManager(ArticleManager articleManager) {
        this.articleManager = articleManager;
    }

    public void setExchangeManager(ExchangeManager exchangeManager) {
        this.exchangeManager = exchangeManager;
    }
}
