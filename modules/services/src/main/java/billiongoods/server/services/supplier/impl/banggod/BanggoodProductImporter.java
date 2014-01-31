package billiongoods.server.services.supplier.impl.banggod;

import au.com.bytecode.opencsv.CSVReader;
import billiongoods.server.services.image.ImageManager;
import billiongoods.server.services.price.MarkupType;
import billiongoods.server.services.price.PriceConverter;
import billiongoods.server.services.supplier.DataLoadingException;
import billiongoods.server.services.supplier.ImportingSummary;
import billiongoods.server.services.supplier.ProductImporter;
import billiongoods.server.services.supplier.SupplierDataLoader;
import billiongoods.server.services.supplier.impl.DefaultImportingSummary;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodProductImporter implements ProductImporter {
	private SupplierDataLoader priceLoader;
	private ImageManager imageManager;
	private ProductManager productManager;
	private PriceConverter priceConverter;
	private RelationshipManager relationshipManager;

	private AsyncTaskExecutor taskExecutor;
	private PlatformTransactionManager transactionManager;

	private DefaultImportingSummary importingSummary = null;

	private static final DefaultTransactionAttribute NEW_TRANSACTION_DEFINITION = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

	private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.BanggoodProductImporter");

	public BanggoodProductImporter() {
	}

	@Override
	public synchronized ImportingSummary getImportingSummary() {
		return importingSummary;
	}

	public synchronized ImportingSummary importProducts(Category category,
														List<Property> properties, List<Integer> groups,
														InputStream descStream, InputStream imagesStream,
														final boolean validatePrice) throws IOException {
		if (importingSummary == null) {
			final List<SuppliedProduct> products = parseProducts(descStream);
			final Map<String, Set<String>> images = parseImages(imagesStream);

			importingSummary = new DefaultImportingSummary(category, properties, groups, products.size());
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					importProducts(products, images, validatePrice);
				}
			});
		}
		return importingSummary;
	}

	private void importProducts(List<SuppliedProduct> products, Map<String, Set<String>> images, boolean validatePrice) {
		try {
			log.info("Start products importing: {}", products.size());

			priceLoader.initialize();

			int index = 0;
			int totalCount = products.size();
			for (SuppliedProduct product : products) {
				log.info("Importing supplied [{}] of [{}]: {} from {}", index++, totalCount, product.sku, product.uri);
				importProduct(product, images.get(product.sku), validatePrice);
			}
			log.info("All products imported");
		} finally {
			synchronized (this) {
				importingSummary = null;
			}
		}
	}

	private void importProduct(SuppliedProduct supplied, Set<String> images, boolean validatePrice) {
		final TransactionStatus transaction = transactionManager.getTransaction(NEW_TRANSACTION_DEFINITION);
		try {
			if (productManager.searchBySku(supplied.sku) == null) {
				Price supplierPrice = supplied.price;
				if (validatePrice) {
					try {
						supplierPrice = priceLoader.loadDescription(supplied).getPrice();
						log.info("Price has been loaded: {}", supplierPrice);
					} catch (DataLoadingException ex) {
						log.info("Price can't be loaded", ex);
					}
				}
				final Price price = priceConverter.convert(supplierPrice, MarkupType.REGULAR);

				final Category category = importingSummary.getImportingCategory();
				final List<Property> properties = importingSummary.getProperties();

				final ProductEditor editor = new ProductEditor();
				editor.setName(supplied.name);
				editor.setDescription(supplied.desc);
				editor.setCategoryId(category.getId());
				editor.setPrice(price);
				editor.setWeight(supplied.weight);
				editor.setProperties(properties);
				editor.setReferenceUri(supplied.uri);
				editor.setReferenceCode(supplied.sku);
				editor.setWholesaler(Supplier.BANGGOOD);
				editor.setSupplierPrice(supplierPrice);
				editor.setProductState(ProductState.DISABLED);

				final Product product = productManager.createProduct(editor);
				log.info("Product imported: {}", product.getId());

				if (images != null) {
					int index = 1;
					final List<String> codes = new ArrayList<>(images.size());
					for (String url : images) {
						final String code = String.valueOf(index++);
						log.info("Importing [{} of {}] image {} from {}", index - 1, images.size(), code, url);
						try (InputStream inputStream = new URL(url).openStream()) {
							imageManager.addImage(product, code, inputStream);
							codes.add(code);
							log.info("Product associated with image: {}", code);
						} catch (Exception ex) {
							log.error("SKU image can't be imported", ex);
						}
					}

					if (!codes.isEmpty()) {
						editor.setImageIds(codes);
						editor.setPreviewImage(codes.iterator().next());
						productManager.updateProduct(product.getId(), editor);
					}
					log.info("Product associated with images: {}", codes);
				}

				final List<Integer> groups = importingSummary.getGroups();
				if (groups != null) {
					for (Integer group : groups) {
						relationshipManager.addGroupItem(group, product.getId());
					}
					log.info("Product associated with groups: {}", groups);
				}
				importingSummary.incrementImported();
			} else {
				importingSummary.incrementSkipped();
			}
			transactionManager.commit(transaction);
		} catch (Exception ex) {
			log.error("Product can't be imported: " + supplied, ex);
			importingSummary.incrementBroken();
			transactionManager.rollback(transaction);
		}
	}

	protected String cleanSpan(String s) {
		String res = s;
		int iteration = 0;
		while (res.contains("</span>") && iteration < 10) {
			res = res.replaceAll("<span[^>]*?>(.*?)</span>", "$1");
			iteration++;
		}
		return res;
	}

	protected List<SuppliedProduct> parseProducts(InputStream stream) throws IOException {
		final List<SuppliedProduct> res = new ArrayList<>();

		final CSVReader reader = new CSVReader(new InputStreamReader(stream));
		String[] nextLine = reader.readNext(); // ignore header
		while ((nextLine = reader.readNext()) != null) {
			final String sku = nextLine[0];
			try {
				final String name = nextLine[1];
				final double price = Double.parseDouble(nextLine[3]);
				final double weight = Double.parseDouble(nextLine[4]);
				final String desc = cleanSpan(nextLine[5]);
				final URL url = new URL(nextLine[6]);
				final String uri = url.getFile();
				res.add(new SuppliedProduct(sku, url, uri, name, desc, price, weight));
			} catch (NumberFormatException ex) {
				throw new IOException("Product price or weight can't be parsed: " + sku, ex);
			} catch (MalformedURLException ex) {
				throw new IOException("Product url can't be parsed: " + sku, ex);
			}
		}
		return res;
	}

	protected Map<String, Set<String>> parseImages(InputStream stream) throws IOException {
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

	public void setPriceLoader(SupplierDataLoader priceLoader) {
		this.priceLoader = priceLoader;
	}

	public void setImageManager(ImageManager imageManager) {
		this.imageManager = imageManager;
	}

	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	public void setPriceConverter(PriceConverter priceConverter) {
		this.priceConverter = priceConverter;
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

	private static class SuppliedProduct implements SupplierInfo {
		private final String sku;
		private final URL url;
		private final String uri;
		private final String name;
		private final String desc;
		private final Price price;
		private final double weight;

		private SuppliedProduct(String sku, URL url, String uri, String name, String desc, double price, double weight) {
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
		public String getReferenceId() {
			return getWholesaler().getReferenceId(this);
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

		@Override
		public String toString() {
			return "SuppliedProduct{" +
					"sku='" + sku + '\'' +
					", url=" + url +
					'}';
		}
	}
}
