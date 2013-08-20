package billiongoods.server.warehouse.impl;

import billiongoods.core.search.Range;
import billiongoods.core.task.CleaningDayListener;
import billiongoods.server.services.price.ExchangeManager;
import billiongoods.server.services.price.MarkupCalculator;
import billiongoods.server.warehouse.ArticleManager;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.CategoryManager;
import billiongoods.server.warehouse.SupplierInfo;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodPriceValidator implements CleaningDayListener {
    private SessionFactory sessionFactory;
    private ArticleManager articleManager;
    private ExchangeManager exchangeManager;
    private CategoryManager categoryManager;

    private volatile boolean active = false;

    private static final int BULK_ARTICLES_SIZE = 100;

    private static final Pattern PRICE_PATTERN = Pattern.compile("<div (?:[^\\s]+).*?id=\"(price_sub|regular_div)\".*?>\\(?(.+?)\\)?</div>");

    private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.BanggoodPriceValidator");

    public BanggoodPriceValidator() {
    }

    private int doValidation() {
        final List<Category> categories = collectFinalCategory(categoryManager.getCatalog().getRootCategories(), null);
        log.info("Validation prices: Found {} categories for checking", categories.size());

        final MarkupCalculator markupCalculator = exchangeManager.getMarkupCalculator();

        int brokenArticles = 0;
        int updateArticles = 0;
        int processedArticles = 0;
        for (Category category : categories) {
            log.info("Processing category {} ({})", category.getId(), category.getName());

            int index = 0;
            while (true) {
                final Range range = Range.limit(index, BULK_ARTICLES_SIZE);

                final Session session = sessionFactory.getCurrentSession();
                final Query query = session.createQuery("select a.id, a.price, a.primordialPrice, a.supplierInfo from billiongoods.server.warehouse.impl.HibernateArticle a where a.active=:active and a.categoryId=:cat order by a.id");
                query.setParameter("cat", category.getId());
                query.setParameter("active", Boolean.TRUE);
                range.apply(query);

                final List list = query.list();
                if (list.size() == 0) {
                    break;
                }

                for (Object o : list) {
                    processedArticles++;
                    final Object[] a = (Object[]) o;

                    final Integer articleId = (Integer) a[0];
                    final SupplierInfo supplierInfo = (SupplierInfo) a[3];

                    HibernatePriceValidation v = new HibernatePriceValidation(articleId, (Double) a[1], (Double) a[2], supplierInfo);

                    try {
                        final URL url = new URL("http://www.banggood.com/-p-" + supplierInfo.getReferenceId() + ".html");

                        final URLConnection urlConnection = url.openConnection();
                        final InputStream inputStream = urlConnection.getInputStream();
                        final Price parsedPrice = parsePrice(inputStream);
                        inputStream.close();

                        if (parsedPrice == null) {
                            throw new IllegalStateException("Supplier price wasn't parsed");
                        } else {
                            boolean update = v.validate(markupCalculator, parsedPrice.price, parsedPrice.primordialPrice);
                            if (update) {
                                updateArticles++;
                                log.info("Price for article has been updated: {}", v);

                                articleManager.updatePrice(v.getArticleId(), v.getNewPrice(), v.getNewPrimordialPrice(), v.getNewSupplierPrice(), v.getNewSupplierPrimordialPrice());
                                session.save(v);
                            }
                        }
                    } catch (Exception ex) {
                        brokenArticles++;
                        log.error("Article price can't be processed: " + articleId, ex);
                    }
                }
                index += BULK_ARTICLES_SIZE;
            }
        }

        log.info("Validation prices done: processed - {}, update - {}, broken - {}", processedArticles, updateArticles, brokenArticles);
        return updateArticles;
    }

    public int validatePrice() {
        synchronized (this) {
            if (active) {
                return -1;
            }
            active = true;
        }
        int res = doValidation();
        synchronized (this) {
            active = false;
        }
        return res;
    }

    public synchronized boolean isActive() {
        return active;
    }

    private Price parsePrice(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        Double price = null;
        Double primordialPrice = null;

        String s = reader.readLine();
        while (s != null) {
            final Matcher matcher = PRICE_PATTERN.matcher(s.trim());
            if (matcher.matches()) {
                final String type = matcher.group(1);
                final String p = matcher.group(2);

                if ("price_sub".equals(type)) {
                    price = Double.parseDouble(p);
                }
                if ("regular_div".equals(type)) {
                    primordialPrice = Double.parseDouble(p);
                }
            }
            // id="price_sub" - price
            // id="regular_div" - primordialPrice
            s = reader.readLine();
        }
        if (price != null) {
            return new Price(price, primordialPrice);
        }
        return null;
    }

    private List<Category> collectFinalCategory(List<Category> categories, List<Category> res) {
        if (res == null) {
            res = new ArrayList<>();
        }
        for (Category category : categories) {
            if (category.isFinal()) {
                res.add(category);
            } else {
                collectFinalCategory(category.getChildren(), res);
            }
        }
        return res;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setArticleManager(ArticleManager articleManager) {
        this.articleManager = articleManager;
    }

    public void setCategoryManager(CategoryManager categoryManager) {
        this.categoryManager = categoryManager;
    }

    public void setExchangeManager(ExchangeManager exchangeManager) {
        this.exchangeManager = exchangeManager;
    }

    @Override
    public void cleanup(Date today) {
        validatePrice();
    }

    private static class Price {
        private Double price;
        private Double primordialPrice;

        private Price(Double price, Double primordialPrice) {
            this.price = price;
            this.primordialPrice = primordialPrice;
        }
    }
}
