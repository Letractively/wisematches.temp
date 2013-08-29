package billiongoods.server.services.arivals.impl;

import au.com.bytecode.opencsv.CSVReader;
import billiongoods.server.services.arivals.ArticleImporter;
import billiongoods.server.services.arivals.ImportingSummary;
import billiongoods.server.services.image.ImageManager;
import billiongoods.server.services.price.ExchangeManager;
import billiongoods.server.services.price.impl.PriceLoader;
import billiongoods.server.services.price.impl.PriceLoadingException;
import billiongoods.server.warehouse.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodArticleImporter implements ArticleImporter {
	private PriceLoader priceLoader;
	private ImageManager imageManager;
	private ArticleManager articleManager;
	private ExchangeManager exchangeManager;
	private RelationshipManager relationshipManager;

	private AsyncTaskExecutor taskExecutor;
	private PlatformTransactionManager transactionManager;

	private DefaultImportingSummary importingSummary = null;

	private static final DefaultTransactionAttribute NEW_TRANSACTION_DEFINITION = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

	private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.BanggoodArticleImporter");

	public BanggoodArticleImporter() {
	}

	@Override
	public synchronized ImportingSummary getImportingSummary() {
		return importingSummary;
	}

	public synchronized ImportingSummary importArticles(Category category,
														List<Property> properties, List<Integer> groups,
														InputStream descStream, InputStream imagesStream,
														final boolean validatePrice) throws IOException {
		if (importingSummary == null) {
			final List<SuppliedArticle> articles = parseArticles(descStream);
			final Map<String, Set<String>> images = parseImages(imagesStream);

			importingSummary = new DefaultImportingSummary(category, properties, groups, articles.size());
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					importArticles(articles, images, validatePrice);
				}
			});
		}
		return importingSummary;
	}

	private void importArticles(List<SuppliedArticle> articles, Map<String, Set<String>> images, boolean validatePrice) {
		try {
			log.info("Start articles importing: {}", articles.size());
			int index = 0;
			int totalCount = articles.size();
			for (SuppliedArticle article : articles) {
				log.info("Importing supplied [{}] of [{}]: {} from {}", index++, totalCount, article.sku, article.uri);
				importArticle(article, images.get(article.sku), validatePrice);
			}
			log.info("All articles imported");
		} finally {
			synchronized (this) {
				importingSummary = null;
			}
		}
	}

	private void importArticle(SuppliedArticle supplied, Set<String> images, boolean validatePrice) {
		final TransactionStatus transaction = transactionManager.getTransaction(NEW_TRANSACTION_DEFINITION);
		try {
			final Article article1 = articleManager.getArticle(supplied.sku);
			if (article1 == null) {
				Price supplierPrice = supplied.price;
				if (validatePrice) {
					try {
						supplierPrice = priceLoader.loadPrice(supplied);
						log.info("Price has been loaded: {}", supplierPrice);
					} catch (PriceLoadingException ex) {
						log.info("Price can't be loaded", ex);
					}
				}
				final Price price = exchangeManager.getMarkupCalculator().calculateMarkupPrice(supplierPrice);

				final Category category = importingSummary.getImportingCategory();
				final List<Property> properties = importingSummary.getProperties();

				final Article article = articleManager.createArticle(supplied.name, supplied.desc, category, price,
						supplied.weight, null, null, null, null, properties, supplied.uri, supplied.sku, Supplier.BANGGOOD, supplierPrice);
				log.info("Article imported: {}", article.getId());

				if (images != null) {
					int index = 1;
					final List<String> codes = new ArrayList<>(images.size());
					for (String url : images) {
						final String code = String.valueOf(index++);
						log.info("Importing [{} of {}] image {} from {}", index - 1, images.size(), code, url);
						try (InputStream inputStream = new URL(url).openStream()) {
							imageManager.addImage(article, code, inputStream);
							codes.add(code);
							log.info("Article associated with image: {}", code);
						} catch (Exception ex) {
							log.error("SKU image can't be imported", ex);
						}
					}

					if (!codes.isEmpty()) {
						articleManager.updateArticle(article.getId(),
								supplied.name, supplied.desc, category, price,
								supplied.weight, null, codes.iterator().next(), codes, null,
								properties, supplied.uri, supplied.sku, Supplier.BANGGOOD, supplierPrice);
					}
					log.info("Article associated with images: {}", codes);
				}

				final List<Integer> groups = importingSummary.getGroups();
				if (groups != null) {
					for (Integer group : groups) {
						relationshipManager.addGroupItem(group, article.getId());
					}
					log.info("Article associated with groups: {}", groups);
				}
				importingSummary.incrementImported();
			} else {
				importingSummary.incrementSkipped();
			}
			transactionManager.commit(transaction);
		} catch (Exception ex) {
			importingSummary.incrementBroken();
			transactionManager.rollback(transaction);
		}
	}

	private List<SuppliedArticle> parseArticles(InputStream stream) throws IOException {
		final List<SuppliedArticle> res = new ArrayList<>();

		final CSVReader reader = new CSVReader(new InputStreamReader(stream));
		String[] nextLine = reader.readNext(); // ignore header
		while ((nextLine = reader.readNext()) != null) {
			final String sku = nextLine[0];
			final String name = nextLine[1];
			final double price = Double.parseDouble(nextLine[3]);
			final double weight = Double.parseDouble(nextLine[4]);
			final String desc = nextLine[5];
			final URL url = new URL(nextLine[6]);
			final String uri = url.getFile();
			res.add(new SuppliedArticle(sku, url, uri, name, desc, price, weight));
		}
		return res;
	}

	private Map<String, Set<String>> parseImages(InputStream stream) throws IOException {
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

	public void setPriceLoader(PriceLoader priceLoader) {
		this.priceLoader = priceLoader;
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

	public void setRelationshipManager(RelationshipManager relationshipManager) {
		this.relationshipManager = relationshipManager;
	}

	public void setTaskExecutor(AsyncTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	private static class SuppliedArticle implements SupplierInfo {
		private final String sku;
		private final URL url;
		private final String uri;
		private final String name;
		private final String desc;
		private final Price price;
		private final double weight;

		private SuppliedArticle(String sku, URL url, String uri, String name, String desc, double price, double weight) {
			this.sku = sku;
			this.url = url;
			this.uri = uri;
			this.name = name;
			this.desc = desc;
			this.price = new Price(price);
			this.weight = weight;
		}

		@Override
		public Price getPrice() {
			return price;
		}

		@Override
		public URL getReferenceUrl() {
			return url;
		}

		@Override
		public String getReferenceUri() {
			return uri;
		}

		@Override
		public String getReferenceCode() {
			return sku;
		}

		@Override
		public Supplier getWholesaler() {
			return Supplier.BANGGOOD;
		}

		@Override
		public Date getValidationDate() {
			return null;
		}
	}
}
