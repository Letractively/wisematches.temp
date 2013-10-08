package billiongoods.server.services.price.impl;

import billiongoods.core.search.Range;
import billiongoods.core.task.CleaningDayListener;
import billiongoods.server.services.price.*;
import billiongoods.server.services.supplier.Availability;
import billiongoods.server.services.supplier.DataLoadingException;
import billiongoods.server.services.supplier.SupplierDataLoader;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.ProductContext;
import billiongoods.server.warehouse.ProductManager;
import billiongoods.server.warehouse.SupplierInfo;
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
public class HibernatePriceValidator implements PriceValidator, CleaningDayListener {
	private SupplierDataLoader priceLoader;

	private SessionFactory sessionFactory;
	private ProductManager productManager;

	private PriceConverter priceConverter;
	private ExchangeManager exchangeManager;

	private AsyncTaskExecutor taskExecutor;
	private PlatformTransactionManager transactionManager;

	private Future validationProgress;

	private final ReusableValidationSummary validationSummary = new ReusableValidationSummary();

	private static final int BULK_PRODUCTS_SIZE = 10;

	private final Collection<PriceValidatorListener> listeners = new CopyOnWriteArrayList<>();

	private static final DefaultTransactionAttribute NEW_TRANSACTION_DEFINITION = new DefaultTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

	private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.PriceValidator");


	public HibernatePriceValidator() {
	}

	@Override
	public void addPriceValidatorListener(PriceValidatorListener l) {
		if (l != null) {
			listeners.add(l);
		}
	}

	@Override
	public void removePriceValidatorListener(PriceValidatorListener l) {
		if (l != null) {
			listeners.remove(l);
		}
	}

	private void doValidation() {
		try {
			final Date startedDate = new Date();
			final double exchangeRate = exchangeManager.getExchangeRate();

			validationSummary.initialize(startedDate);

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

			for (PriceValidatorListener listener : listeners) {
				listener.priceValidationStarted(startedDate, count);
			}

			priceLoader.initialize();

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

					final Query query = session.createQuery("select a.id, a.price, a.supplierInfo from billiongoods.server.warehouse.impl.HibernateProduct a where a.state in (:states) order by a.id");
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
						final SupplierInfo supplierInfo = (SupplierInfo) a[2];

						final Price oldPrice = (Price) a[1];
						final Price oldSupplierPrice = supplierInfo.getPrice();

						Availability availability = null;
						Price newSupplierPrice = oldSupplierPrice;
						try {
							newSupplierPrice = priceLoader.loadDescription(supplierInfo).getPrice();
						} catch (DataLoadingException ex) {
							log.info("Price for product {} can't be updated: {}", productId, ex.getMessage());
							validationSummary.addBreakdown(new Date(), productId, ex);
						}
						try {
							availability = priceLoader.loadAvailability(supplierInfo);
						} catch (DataLoadingException ex) {
							log.info("Price for product {} can't be updated: {}", productId, ex.getMessage());
							validationSummary.addBreakdown(new Date(), productId, ex);
						}

						final Price newPrice = priceConverter.convert(newSupplierPrice, exchangeRate, MarkupType.REGULAR);
						final HibernatePriceRenewal renewal = new HibernatePriceRenewal(productId, new Date(), oldPrice, oldSupplierPrice, newPrice, newSupplierPrice);
						if (!newPrice.equals(oldPrice)) {
							log.info("Price for product {} from {} has been updated {}", productId, supplierInfo.getReferenceUri(), renewal);
							validationSummary.addRenewal(renewal);

							session.save(renewal);
							productManager.updateSupplier(productId, newPrice, newSupplierPrice, availability);
						}

						for (PriceValidatorListener listener : listeners) {
							listener.priceValidated(productId, renewal);
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
			for (PriceValidatorListener listener : listeners) {
				listener.priceValidationFinished(finishedDate, validationSummary);
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

	public void setPriceLoader(SupplierDataLoader priceLoader) {
		this.priceLoader = priceLoader;
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
