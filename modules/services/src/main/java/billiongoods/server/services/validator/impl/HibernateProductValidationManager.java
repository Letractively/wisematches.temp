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

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateProductValidationManager implements ProductValidationManager, CleaningDayListener {
    private SessionFactory sessionFactory;
    private ProductManager productManager;

    private Future validationProgress;
    private AsyncTaskExecutor taskExecutor;

    private PriceConverter priceConverter;
    private ExchangeManager exchangeManager;

    private SupplierDataLoader dataLoader;

    private PlatformTransactionManager transactionManager;
    private final ReusableValidationSummary validationSummary = new ReusableValidationSummary();

    private final Collection<ValidationProgressListener> listeners = new CopyOnWriteArrayList<>();

    private static final int BULK_PRODUCTS_SIZE = 10;
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
        final Session session = sessionFactory.openSession();
        try {
            final Query countQuery = session.createQuery("select count(*) from billiongoods.server.warehouse.impl.HibernateProduct a where a.state in (:states)");
            countQuery.setParameterList("states", ProductContext.VISIBLE);

            final int totalProductsCount = ((Number) countQuery.uniqueResult()).intValue();

            validationSummary.initialize(new Date(), totalProductsCount);

            for (ValidationProgressListener listener : listeners) {
                listener.validationStarted(validationSummary.getStartDate(), validationSummary.getTotalCount());
            }

            final List<ProductDetails> brokenProducts = new ArrayList<>();
            while (validationSummary.getIteration() < 5 && !isInterrupted()) {
                log.info("Start iteration {}", validationSummary.getIteration());

                dataLoader.initialize();

                final List<HibernateProductValidation> validations = new ArrayList<>();
                if (validationSummary.getIteration() == 0) { // first iteration - load from DB
                    int position = 0;
                    List<ProductDetails> details = loadProductDetails(session, position, BULK_PRODUCTS_SIZE);
                    while (details != null && !isInterrupted()) {
                        for (Iterator<ProductDetails> iterator = details.iterator(); iterator.hasNext() && !isInterrupted(); ) {
                            ProductDetails detail = iterator.next();
                            final HibernateProductValidation validation = validateProduct(detail);
                            if (validation == null || !validation.isValidated()) {
                                brokenProducts.add(detail);
                                validationSummary.incrementBroken();
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
                    try {
                        Thread.sleep(TimeUnit.MINUTES.toMillis(30)); // Wait 30 minutes
                    } catch (InterruptedException ex) {
                        break;
                    }

                    Collections.shuffle(brokenProducts);
                    for (Iterator<ProductDetails> iterator = brokenProducts.iterator(); iterator.hasNext() && !isInterrupted(); ) {
                        final ProductDetails detail = iterator.next();

                        final HibernateProductValidation validation = validateProduct(detail);
                        if (validation == null || !validation.isValidated()) {
                            validationSummary.incrementBroken();
                        } else if (validation.hasChanges()) {
                            validations.add(validation);
                            validationSummary.registerValidation(validation);
                            iterator.remove();
                        }
                        validationSummary.incrementProcessed();
                    }
                }

                final TransactionStatus transaction = transactionManager.getTransaction(NEW_TRANSACTION_DEFINITION);
                try {
                    for (HibernateProductValidation validation : validations) {
                        session.save(validation);
                        productManager.validated(validation.getProductId(), validation.getNewPrice(), validation.getNewSupplierPrice(), validation.getNewStockInfo());

                        for (ValidationProgressListener listener : listeners) {
                            listener.productValidated(validation.getProductId(), validation);
                        }
                    }
                    session.flush();
                    transactionManager.commit(transaction);
                } catch (Exception ex) {
                    transactionManager.rollback(transaction);
                }
                validationSummary.incrementIteration(brokenProducts.size());
            }

            validationSummary.finalize(new Date());
            for (ValidationProgressListener listener : listeners) {
                listener.validationFinished(validationSummary.getFinishDate(), validationSummary);
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

    private List<ProductDetails> loadProductDetails(Session session, int position, int count) {
        final Range range = Range.limit(position, count);
        final Query query = session.createQuery("select a.id, a.price, a.stockInfo, a.supplierInfo from billiongoods.server.warehouse.impl.HibernateProduct a where a.state in (:states) order by a.id");
        query.setParameterList("states", ProductContext.VISIBLE);
        final List list = range.apply(query).list();
        if (list.size() == 0) {
            return null;
        }

        final List<ProductDetails> res = new ArrayList<>(count);
        for (Object o : list) {
            final Object[] a = (Object[]) o;

            final Integer productId = (Integer) a[0];
            final Price price = (Price) a[1];
            final StockInfo stockInfo = (StockInfo) a[2];
            final SupplierInfo supplierInfo = (SupplierInfo) a[3];

            res.add(new ProductDetails(productId, price, stockInfo, supplierInfo));
        }
        return res;
    }

    private HibernateProductValidation validateProduct(ProductDetails detail) {
        try {
            final SupplierDescription description = dataLoader.loadDescription(detail.getSupplierInfo());
            if (description != null) {
                final HibernateProductValidation validation = new HibernateProductValidation(detail.getId(), detail.getPrice(), detail.getSupplierInfo().getPrice(), detail.getStockInfo());

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

    private static final class ProductDetails {
        private final Integer id;
        private final Price price;
        private final StockInfo stockInfo;
        private final SupplierInfo supplierInfo;

        private ProductDetails(Integer id, Price price, StockInfo stockInfo, SupplierInfo supplierInfo) {
            this.id = id;
            this.price = price;
            this.stockInfo = stockInfo;
            this.supplierInfo = supplierInfo;
        }

        public Integer getId() {
            return id;
        }

        public Price getPrice() {
            return price;
        }

        public StockInfo getStockInfo() {
            return stockInfo;
        }

        public SupplierInfo getSupplierInfo() {
            return supplierInfo;
        }
    }
}
