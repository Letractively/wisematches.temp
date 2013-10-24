package billiongoods.server.services.validator.impl;

import billiongoods.core.search.Range;
import billiongoods.core.task.CleaningDayListener;
import billiongoods.server.services.price.ExchangeManager;
import billiongoods.server.services.price.MarkupType;
import billiongoods.server.services.price.PriceConverter;
import billiongoods.server.services.supplier.DataLoadingException;
import billiongoods.server.services.supplier.SupplierDataLoader;
import billiongoods.server.services.supplier.SupplierDescription;
import billiongoods.server.services.validator.ProductValidationManager;
import billiongoods.server.services.validator.ValidationProgressListener;
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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateProductValidationManager implements ProductValidationManager, CleaningDayListener {
	private SessionFactory sessionFactory;
	private ProductManager productManager;

	private PriceConverter priceConverter;
	private ExchangeManager exchangeManager;

	private SupplierDataLoader dataLoader;

	private AsyncTaskExecutor taskExecutor;
	private PlatformTransactionManager transactionManager;

	private Future validationProgress;

	private final ReusableValidationSummary validationSummary = new ReusableValidationSummary();

	private static final int BULK_PRODUCTS_SIZE = 10;

	private final Collection<ValidationProgressListener> listeners = new CopyOnWriteArrayList<>();

	private static final DefaultTransactionAttribute NEW_TRANSACTION_DEFINITION = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

	private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.ProductValidationManager");


	public HibernateProductValidationManager() {
	}

	@Override
	public void addValidationProgressListener(ValidationProgressListener l) {
		if (l != null) {
			listeners.add(l);
		}
	}

	@Override
	public void removeValidationProgressListener(ValidationProgressListener l) {
		if (l != null) {
			listeners.remove(l);
		}
	}

	private void doValidation() {
		try {
			final Date startedDate = new Date();
			final double exchangeRate = exchangeManager.getExchangeRate();

			int count;
			TransactionStatus transaction = transactionManager.getTransaction(NEW_TRANSACTION_DEFINITION);
			try {
				Session session = sessionFactory.getCurrentSession();
				final Query countQuery = session.createQuery("select count(*) from billiongoods.server.warehouse.impl.HibernateProduct a where a.state in (:states)");
				countQuery.setParameterList("states", ProductContext.VISIBLE);

				count = ((Number) countQuery.uniqueResult()).intValue();
				transactionManager.commit(transaction);
			} catch (Exception ex) {
				transactionManager.rollback(transaction);
				throw ex;
			}

			for (ValidationProgressListener listener : listeners) {
				listener.validationStarted(startedDate, count);
			}

			validationSummary.initialize(startedDate, count);

			dataLoader.initialize();

			int index = 0;
			while (true) {
				synchronized (this) {
					if (validationProgress == null || validationProgress.isCancelled() || validationProgress.isDone()) {
						log.info("Validation progress was interrupted");
						break;
					}
				}
				final Range range = Range.limit(index, BULK_PRODUCTS_SIZE);

				transaction = transactionManager.getTransaction(new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
				try {
					Session session = sessionFactory.getCurrentSession();

					final Query query = session.createQuery("select a.id, a.price, a.stockInfo, a.supplierInfo from billiongoods.server.warehouse.impl.HibernateProduct a where a.state in (:states) order by a.id");
					query.setParameterList("states", ProductContext.VISIBLE);
					final List list = range.apply(query).list();
					if (list.isEmpty()) {
						transactionManager.commit(transaction);
						break;
					}
					for (Object o : list) {
						validationSummary.incrementValidated();

						final Object[] a = (Object[]) o;

						final Integer productId = (Integer) a[0];
						final Price oldPrice = (Price) a[1];
						final StockInfo oldStockInfo = (StockInfo) a[2];
						final SupplierInfo supplierInfo = (SupplierInfo) a[3];

						final Price oldSupplierPrice = supplierInfo.getPrice();

						final HibernateProductValidation validation = new HibernateProductValidation(productId, new Date(), oldPrice, oldSupplierPrice, oldStockInfo);

						try {
							final SupplierDescription description = dataLoader.loadDescription(supplierInfo);
							if (description != null) {
								final Price newSupplierPrice = description.getPrice();
								if (newSupplierPrice != null) {
									final Price newPrice = priceConverter.convert(newSupplierPrice, exchangeRate, MarkupType.REGULAR);
									validation.priceValidated(newPrice, newSupplierPrice);
								}

								if (description.getStockInfo() != null) {
									validation.stockValidated(description.getStockInfo());
								}
							}
						} catch (DataLoadingException ex) {
							validation.processingError(ex);
							log.info("Price for product {} can't be updated: {}", productId, ex.getMessage());
						}

						if (validation.getErrorMessage() != null) {
							validationSummary.addProductValidation(validation);
						} else if (validation.hasChanges()) {
							log.info("Product {} has changes: {}", productId, validation);
							validationSummary.addProductValidation(validation);

							session.save(validation);
							productManager.validated(productId, validation.getNewPrice(), validation.getNewSupplierPrice(), validation.getNewStockInfo());
						}

						for (ValidationProgressListener listener : listeners) {
							listener.productValidated(productId, validation);
						}
					}
					session.flush();
					transactionManager.commit(transaction);
				} catch (Exception ex) {
					log.error("Bulk checks can't be processed for range: " + range, ex);
					transactionManager.rollback(transaction);
					break;
				}
				index += BULK_PRODUCTS_SIZE;
			}

			final Date finishedDate = new Date();

			validationSummary.finalize(finishedDate);
			for (ValidationProgressListener listener : listeners) {
				listener.validationFinished(finishedDate, validationSummary);
			}
		} catch (Exception ex) {
			log.error("Price validation can't be done", ex);
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
