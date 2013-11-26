package billiongoods.server.services.validator.impl;

import billiongoods.core.search.Range;
import billiongoods.core.task.CleaningDayListener;
import billiongoods.server.services.price.ExchangeManager;
import billiongoods.server.services.price.MarkupType;
import billiongoods.server.services.price.PriceConverter;
import billiongoods.server.services.supplier.DataLoadingException;
import billiongoods.server.services.supplier.SupplierDataLoader;
import billiongoods.server.services.supplier.SupplierDescription;
import billiongoods.server.services.validator.ValidatingProduct;
import billiongoods.server.services.validator.ValidationListener;
import billiongoods.server.services.validator.ValidationManager;
import billiongoods.server.services.validator.ValidationSummary;
import billiongoods.server.warehouse.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateValidationManager implements ValidationManager, CleaningDayListener {
	private SessionFactory sessionFactory;
	private ProductManager productManager;

	private Future validationProgress;
	private AsyncTaskExecutor taskExecutor;

	private PriceConverter priceConverter;
	private ExchangeManager exchangeManager;

	private SupplierDataLoader dataLoader;

	private PlatformTransactionManager transactionManager;
	private final ReusableValidationSummary validationSummary = new ReusableValidationSummary();

	private final Collection<ValidationListener> listeners = new CopyOnWriteArrayList<>();

	private static final int BULK_PRODUCTS_SIZE = 10;
	private static final DefaultTransactionAttribute NEW_TRANSACTION_DEFINITION = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

	private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.ValidationManager");


	public HibernateValidationManager() {
	}

	@Override
	public void addValidationProgressListener(ValidationListener l) {
		if (l != null) {
			listeners.add(l);
		}
	}

	@Override
	public void removeValidationProgressListener(ValidationListener l) {
		if (l != null) {
			listeners.remove(l);
		}
	}

	private void doValidation() {
		final Session session = sessionFactory.openSession();
		try {
			final Query countQuery = session.createQuery("select count(*) from billiongoods.server.warehouse.impl.HibernateProduct a where a.state in (:states)");
			countQuery.setParameterList("states", ProductContext.VISIBLE);

			final int totalProductsCount = ((Number) countQuery.uniqueResult()).intValue();

			validationSummary.initialize(new Date(), totalProductsCount);

			for (ValidationListener listener : listeners) {
				listener.validationStarted(validationSummary);
			}


			final List<ValidatingProduct> brokenProducts = new ArrayList<>();
			while (validationSummary.getIteration() < 1 && !isInterrupted()) {
				log.info("Start iteration {}", validationSummary.getIteration());

				final List<HibernateValidationChange> validations = new ArrayList<>();
				try {
					if (validationSummary.getIteration() == 0) { // first iteration - load from DB
						int position = 0;
						dataLoader.initialize();
						List<ValidatingProduct> details = loadProductDetails(session, position, BULK_PRODUCTS_SIZE);
						while (details != null && !isInterrupted()) {
							for (Iterator<ValidatingProduct> iterator = details.iterator(); iterator.hasNext() && !isInterrupted(); ) {
								ValidatingProduct detail = iterator.next();
								final HibernateValidationChange validation = validateProduct(detail);
								if (validation == null || !validation.isValidated()) {
									brokenProducts.add(detail);
									validationSummary.incrementBroken();
									Thread.sleep(100);
								} else if (validation.hasChanges()) {
									validations.add(validation);
									validationSummary.registerValidation(validation);
								}
								validationSummary.incrementProcessed();
							}
							position += BULK_PRODUCTS_SIZE;
							details = loadProductDetails(session, position, BULK_PRODUCTS_SIZE);
						}
					} else { // next iteration - process only broken
						log.info("Waiting 30 minutes before next iteration.");
						Thread.sleep(TimeUnit.MINUTES.toMillis(30)); // Wait 30 minutes

						dataLoader.initialize();
						Collections.shuffle(brokenProducts);

						for (Iterator<ValidatingProduct> iterator = brokenProducts.iterator(); iterator.hasNext() && !isInterrupted(); ) {
							final ValidatingProduct detail = iterator.next();

							final HibernateValidationChange validation = validateProduct(detail);
							if (validation != null && validation.isValidated()) {
								iterator.remove();

								if (validation.hasChanges()) {
									validations.add(validation);
									validationSummary.registerValidation(validation);
								}
							} else {
								validationSummary.incrementBroken();
							}
							validationSummary.incrementProcessed();
						}
					}
				} catch (InterruptedException ex) {
					break;
				}

				if (!validations.isEmpty()) {
					final TransactionStatus transaction = transactionManager.getTransaction(NEW_TRANSACTION_DEFINITION);
					try {
						for (HibernateValidationChange validation : validations) {
							session.save(validation);
							productManager.validated(validation.getProductId(), validation.getNewPrice(), validation.getNewSupplierPrice(), validation.getNewStockInfo());

							for (ValidationListener listener : listeners) {
								listener.validationProcessed(validation);
							}
						}
						session.flush();
						transactionManager.commit(transaction);
					} catch (Exception ex) {
						log.error("Validate products can't be updated", ex);
						transactionManager.rollback(transaction);
					}
				}

				if (!brokenProducts.isEmpty()) {
					validationSummary.incrementIteration(brokenProducts.size());
				} else {
					break;
				}
			}

			validationSummary.finalize(new Date());

			for (ValidationListener listener : listeners) {
				listener.validationFinished(validationSummary, brokenProducts);
			}
			log.info("Validation has been finished: " + validationSummary);
		} catch (Exception ex) {
			log.error("Validation error found", ex);
		} finally {
			session.close();
		}

		synchronized (this) {
			validationProgress = null;
		}
	}

	@Override
	public synchronized void startPriceValidation() {
		if (isInProgress()) {
			return;
		}
		log.info("Validation progress was interrupted");

		validationProgress = taskExecutor.submit(new Runnable() {
			@Override
			public void run() {
				doValidation();
			}
		});
	}

	@Override
	public synchronized void stopPriceValidation() {
		if (validationProgress != null) {
			validationProgress.cancel(true);
			validationProgress = null;
		}
	}

	@Override
	public synchronized boolean isInProgress() {
		return validationProgress != null;
	}

	@Override
	public synchronized ValidationSummary getValidationSummary() {
		return validationSummary;
	}

	@Override
	public void cleanup(Date today) {
		startPriceValidation();
	}

	private synchronized boolean isInterrupted() {
		return validationProgress == null || validationProgress.isCancelled() || validationProgress.isDone();
	}

	private List<ValidatingProduct> loadProductDetails(Session session, int position, int count) {
		final Range range = Range.limit(position, count);
		final Query query = session.createQuery("select a.id, a.price, a.stockInfo, a.supplierInfo from billiongoods.server.warehouse.impl.HibernateProduct a where a.state in (:states) order by a.id");
		query.setParameterList("states", ProductContext.VISIBLE);
		final List list = range.apply(query).list();
		if (list.size() == 0) {
			return null;
		}

		final List<ValidatingProduct> res = new ArrayList<>(count);
		for (Object o : list) {
			final Object[] a = (Object[]) o;

			final Integer productId = (Integer) a[0];
			final Price price = (Price) a[1];
			final StockInfo stockInfo = (StockInfo) a[2];
			final SupplierInfo supplierInfo = (SupplierInfo) a[3];

			res.add(new ValidatingProductImpl(productId, price, stockInfo, supplierInfo));
		}
		return res;
	}

	private HibernateValidationChange validateProduct(ValidatingProduct detail) {
		try {
			final SupplierDescription description = dataLoader.loadDescription(detail.getSupplierInfo());
			if (description != null) {
				final HibernateValidationChange validation = new HibernateValidationChange(detail.getId(), detail.getPrice(), detail.getSupplierInfo().getPrice(), detail.getStockInfo());

				final Price supplierPrice = description.getPrice();
				final StockInfo stockInfo = description.getStockInfo();
				if (supplierPrice != null && stockInfo != null) {
					final Price price = priceConverter.convert(supplierPrice, exchangeManager.getExchangeRate(), MarkupType.REGULAR);
					validation.validated(price, supplierPrice, stockInfo);
				}
				return validation;
			}
			return null;
		} catch (DataLoadingException ex) {
			log.info("Product state can't be loaded: {} - {}", detail.getId(), ex.getMessage());
			return null;
		}
	}

	public void setTaskExecutor(AsyncTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setDataLoader(SupplierDataLoader dataLoader) {
		this.dataLoader = dataLoader;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	public void setPriceConverter(PriceConverter priceConverter) {
		this.priceConverter = priceConverter;
	}

	public void setExchangeManager(ExchangeManager exchangeManager) {
		this.exchangeManager = exchangeManager;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
