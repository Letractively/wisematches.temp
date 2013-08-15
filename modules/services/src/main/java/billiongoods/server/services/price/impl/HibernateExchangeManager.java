package billiongoods.server.services.price.impl;

import billiongoods.server.services.price.ExchangeManager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateExchangeManager implements ExchangeManager, InitializingBean {
    private double exchangeRate;

    private SessionFactory sessionFactory;

    private static final Logger log = LoggerFactory.getLogger("billiongoods.price.ExchangeManager");

    public HibernateExchangeManager() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final Session session = sessionFactory.openSession();

        final Query query = session.createQuery("from billiongoods.server.services.price.impl.HibernateExchangeRate order by timestamp desc");
        query.setMaxResults(1);

        final HibernateExchangeRate rate = (HibernateExchangeRate) query.uniqueResult();
        if (rate == null) {
            exchangeRate = 33.33f;
            log.info("Exchange rate set to predefined: {}", exchangeRate);
        } else {
            exchangeRate = rate.getExchangeRate();
            log.info("Exchange rate load from DB: {}", exchangeRate);
        }
        session.flush();
        session.close();
    }

    @Override
    public double convertPrice(double price) {
        return convertPrice(price, exchangeRate);
    }

    @Override
    public double convertPrice(double price, double exchange) {
        return Math.round(price * exchange);
    }

    @Override
    public double getExchangeRate() {
        return exchangeRate;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void setExchangeRate(double exchangeRate) {
        log.info("Exchange rate changed to from {} to {}", this.exchangeRate, exchangeRate);
        sessionFactory.getCurrentSession().save(new HibernateExchangeRate(exchangeRate));
        this.exchangeRate = exchangeRate;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
