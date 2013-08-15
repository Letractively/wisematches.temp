package billiongoods.server.services.price.impl;

import billiongoods.server.services.price.PriceConverter;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernatePriceConverter extends PriceConverter implements InitializingBean {
	private float exchangeRate;

	private SessionFactory sessionFactory;

	public HibernatePriceConverter() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final Session session = sessionFactory.openSession();

		final Query query = session.createQuery("from billiongoods.server.services.price.impl.HibernateExchangeRate");
		query.setMaxResults(1);

		final HibernateExchangeRate rate = (HibernateExchangeRate) query.uniqueResult();
		if (rate == null) {
			exchangeRate = 33.33f;
		} else {
			exchangeRate = rate.getExchangeRate();
		}
		session.flush();
		session.close();
	}

	@Override
	public float getExchangeRate() {
		return exchangeRate;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void setExchangeRate(float exchangeRate) {
		sessionFactory.getCurrentSession().save(new HibernateExchangeRate(exchangeRate));
		this.exchangeRate = exchangeRate;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
