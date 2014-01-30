package billiongoods.server.services.validator.impl;

import billiongoods.core.search.Range;
import billiongoods.core.task.CleaningDayListener;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
	public void addValidationListener(ValidationListener l) {
		if (l != null) {
			listeners.add(l);
		}
	}

	@Override
	public void removeValidationListener(ValidationListener l) {
		if (l != null) {
			listeners.remove(l);
		}
	}

	@Override
	public synchronized void startValidation() {
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
	public synchronized void cancelValidation() {
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
	@Transactional(propagation = Propagation.MANDATORY)
	public void validateBroken() {
		try {
			final List<ValidatingProduct> brokenProducts = validationSummary.startNextIteration();
			Collections.shuffle(brokenProducts);

			dataLoader.initialize();
			for (Iterator<ValidatingProduct> iterator = brokenProducts.iterator(); iterator.hasNext() && !isInterrupted(); ) {
				final ValidatingProduct product = iterator.next();

				final HibernateValidationChange validation = validateProduct(product);
				if (validation == null || !validation.isValidated()) {
					validationSummary.registerBroken(product);
					Thread.sleep(100);
				} else {
					iterator.remove();

					if (validation.hasChanges()) {
						validationSummary.registerValidation(validation);
						processValidationChange(validation);
					}
				}
				validationSummary.incrementProcessed();
			}
		} catch (InterruptedException ignore) {
		}
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void validateExchangeRate() {
		final Session session = sessionFactory.openSession();

		final String buyPriceF = priceConverter.formula("p.supplierInfo.price.amount", "ROUND", MarkupType.REGULAR);
		final String buyPrimordialPriceF = priceConverter.formula("p.supplierInfo.price.primordialAmount", "ROUND", MarkupType.REGULAR);

		final Query query = session.createQuery("update from billiongoods.server.warehouse.impl.HibernateProduct p set " +
				"p.price.amount = " + buyPriceF + ", " +
				"p.price.primordialAmount = " + buyPrimordialPriceF);
		query.executeUpdate();
	}

	private void doValidation() {
		final Session session = sessionFactory.openSession();
		try {
			final Query countQuery = session.createQuery("select count(*) from billiongoods.server.warehouse.impl.HibernateProduct a where a.state in (:states)");
			countQuery.setParameterList("states", ProductContext.VISIBLE);

			validationSummary.initialize(new Date(), ((Number) countQuery.uniqueResult()).intValue());

			for (ValidationListener listener : listeners) {
				listener.validationStarted(validationSummary);
			}

			int position = 0;
			dataLoader.initialize();
			log.info("Start iteration {}", validationSummary.getIteration());
			List<ValidatingProduct> details = loadProductDetails(session, position, BULK_PRODUCTS_SIZE);
			while (details != null && !isInterrupted()) {
				try {
					for (Iterator<ValidatingProduct> iterator = details.iterator(); iterator.hasNext() && !isInterrupted(); ) {
						ValidatingProduct product = iterator.next();
						final HibernateValidationChange validation = validateProduct(product);
						if (validation == null || !validation.isValidated()) {
							validationSummary.registerBroken(product);
							Thread.sleep(100);
						} else if (validation.hasChanges()) {
							validationSummary.registerValidation(validation);
							processValidationChange(validation);
						}
						validationSummary.incrementProcessed();
					}
					position += BULK_PRODUCTS_SIZE;
					details = loadProductDetails(session, position, BULK_PRODUCTS_SIZE);
				} catch (InterruptedException ignore) {
					break;
				}
			}

			while (!isInterrupted() && validationSummary.getIteration() < 4) {
				try {
					log.info("Waiting 10 minutes before next iteration: {}", validationSummary.getIteration() + 1);
					Thread.sleep(TimeUnit.MINUTES.toMillis(10)); // Wait 30 minutes

					validateBroken();
				} catch (InterruptedException ex) {
					break;
				}
			}

			validationSummary.finalize(new Date());
			log.info("Validation has been finished: " + validationSummary);

			for (ValidationListener listener : listeners) {
				listener.validationFinished(validationSummary);
			}
		} catch (Exception ex) {
			log.error("Validation error found", ex);
		} finally {
			session.close();
		}

		synchronized (this) {
			validationProgress = null;
		}
	}

	private void processValidationChange(HibernateValidationChange validation) {
		final TransactionStatus transaction = transactionManager.getTransaction(NEW_TRANSACTION_DEFINITION);
		try {
			sessionFactory.getCurrentSession().save(validation);
			productManager.updateProductInformation(validation.getProduct().getId(), validation.getNewPrice(), validation.getNewSupplierPrice(), validation.getNewStockInfo());

			for (ValidationListener listener : listeners) {
				listener.validationProcessed(validation);
			}
			transactionManager.commit(transaction);
		} catch (Exception ex) {
			transactionManager.rollback(transaction);
			log.error("Validate products can't be updated", ex);
		}
	}


	@Override
	public void cleanup(Date today) {
		startValidation();
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
				final HibernateValidationChange change = new HibernateValidationChange(detail, detail.getPrice(),
						detail.getSupplierInfo().getPrice(), detail.getStockInfo());

				final Price supplierPrice = description.getPrice();
				final StockInfo stockInfo = description.getStockInfo();
				if (supplierPrice != null && stockInfo != null) {
					final Price price = priceConverter.convert(supplierPrice, MarkupType.REGULAR);
					change.validated(price, supplierPrice, stockInfo);
				}
				return change;
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

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
