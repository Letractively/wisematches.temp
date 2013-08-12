package billiongoods.server.services.paypal.impl;

import billiongoods.server.services.payment.Order;
import billiongoods.server.services.paypal.PayPalTransactionManager;
import billiongoods.server.services.paypal.TransactionPhase;
import billiongoods.server.services.paypal.TransactionResolution;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import urn.ebay.api.PayPalAPI.DoExpressCheckoutPaymentResponseType;
import urn.ebay.api.PayPalAPI.GetExpressCheckoutDetailsResponseType;
import urn.ebay.api.PayPalAPI.SetExpressCheckoutResponseType;

import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateTransactionManager implements PayPalTransactionManager {
    private SessionFactory sessionFactory;

    private static final Logger log = LoggerFactory.getLogger("billiongoods.paypal.TransactionManager");

    public HibernateTransactionManager() {
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public HibernatePayPalTransaction getTransaction(Long id) {
        return (HibernatePayPalTransaction) sessionFactory.getCurrentSession().get(HibernatePayPalTransaction.class, id);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Long beginTransaction(Order order) {
        HibernatePayPalTransaction transaction = new HibernatePayPalTransaction(order.getId());
        return (Long) sessionFactory.getCurrentSession().save(transaction);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void checkoutInitiated(Long tnxId, SetExpressCheckoutResponseType response) {
        final Session session = sessionFactory.getCurrentSession();

        HibernatePayPalTransaction transaction = getTransaction(tnxId);
        if (transaction != null) {


            transaction.setPhase(TransactionPhase.INITIATED);
            session.update(transaction);
        } else {
            log.error("There is no transaction with id: " + tnxId);
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void checkoutValidated(Long tnxId, GetExpressCheckoutDetailsResponseType response) {
        final Session session = sessionFactory.getCurrentSession();

        HibernatePayPalTransaction transaction = getTransaction(tnxId);
        if (transaction != null) {
            transaction.setPhase(TransactionPhase.VALIDATED);
            session.update(transaction);
        } else {
            log.error("There is no transaction with id: " + tnxId);
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void checkoutConfirmed(Long tnxId, DoExpressCheckoutPaymentResponseType response) {
        final Session session = sessionFactory.getCurrentSession();

        HibernatePayPalTransaction transaction = getTransaction(tnxId);
        if (transaction != null) {
            transaction.setPhase(TransactionPhase.CONFIRMED);
            session.update(transaction);
        } else {
            log.error("There is no transaction with id: " + tnxId);
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void commitTransaction(Long tnxId, TransactionResolution resolution) {
        final Session session = sessionFactory.getCurrentSession();

        HibernatePayPalTransaction transaction = getTransaction(tnxId);
        if (transaction != null) {
            transaction.setPhase(TransactionPhase.DONE);
            transaction.setResolution(resolution);
            session.update(transaction);
        } else {
            log.error("There is no transaction with id: " + tnxId);
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Long registerMessage(Map<String, String[]> parameterMap) {
        final Session session = sessionFactory.getCurrentSession();
        return ((Number) session.save(new HibernateIPNMessage(parameterMap))).longValue();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
