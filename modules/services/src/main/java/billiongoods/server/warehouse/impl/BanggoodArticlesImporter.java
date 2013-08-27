package billiongoods.server.warehouse.impl;

import au.com.bytecode.opencsv.CSVReader;
import billiongoods.server.services.image.ImageManager;
import billiongoods.server.services.price.ExchangeManager;
import billiongoods.server.services.price.impl.PriceLoadingException;
import billiongoods.server.services.price.impl.loader.BanggoodPriceLoader;
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
    private BanggoodPriceLoader priceLoader;
    private RelationshipManager relationshipManager;

    private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.BanggoodArticlesImporter");

    public BanggoodArticlesImporter() {
    }

    public void importArticles(Category category, List<Property> properties, List<Integer> groups, InputStream descStream, InputStream imagesStream) throws IOException {
        final Map<String, Set<String>> images = parseImagesLinks(imagesStream);

        final CSVReader reader = new CSVReader(new InputStreamReader(descStream));
        String[] nextLine = reader.readNext(); // ignore header
        while ((nextLine = reader.readNext()) != null) {
            final String sku = nextLine[0];
            final String name = nextLine[1];
            final double weight = Double.parseDouble(nextLine[4]);
            final String desc = nextLine[5];
            final String uri = nextLine[6].substring(24);
            log.info("Importing article {} from {}", sku, uri);

            final Article article1 = articleManager.getArticle(sku);
            if (article1 != null) {
                log.info("Article with the same SKU already exist. Moving on.");
                continue;
            }

            Price supplierPrice = new Price(Double.parseDouble(nextLine[3]), null);
            try {
                supplierPrice = priceLoader.loadPrice(new HibernateSupplierInfo(uri, sku, Supplier.BANGGOOD, supplierPrice));
                log.info("Price has been loaded: {}", supplierPrice);
            } catch (PriceLoadingException ex) {
                log.info("Price can't be loaded", ex);
            }
            final Price price = exchangeManager.getMarkupCalculator().calculateMarkupPrice(supplierPrice);
            final Article article = articleManager.createArticle(name, desc, category, price, weight, null, null, null, null, properties, uri, sku, Supplier.BANGGOOD, supplierPrice);
            log.info("Article imported: {}", article.getId());

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
                        log.info("Article associated with image: {}", code);
                    } catch (Exception ex) {
                        log.error("SKU image can't be imported", ex);
                    }
                }

                if (!codes.isEmpty()) {
                    articleManager.updateArticle(article.getId(), name, desc, category, price, weight, null,
                            codes.iterator().next(), codes, null, properties, uri, sku, Supplier.BANGGOOD, supplierPrice);
                }
            }

            if (groups != null) {
                for (Integer group : groups) {
                    relationshipManager.addGroupItem(group, article.getId());
                    log.info("Article associated with group: {}", group);
                }
            }
        }
        log.info("All articles imported");
    }

    private Map<String, Set<String>> parseImagesLinks(InputStream stream) throws IOException {
        final Map<String, Set<String>> res = new HashMap<>();

        final CSVReader reader = new CSVReader(new InputStreamReader(stream));
        String[] nextLine = reader.readNext(); // ignore header
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

    public void setPriceLoader(BanggoodPriceLoader priceLoader) {
        this.priceLoader = priceLoader;
    }

    public void setRelationshipManager(RelationshipManager relationshipManager) {
        this.relationshipManager = relationshipManager;
    }
}
