package billiongoods.server.services.price.impl;

import billiongoods.server.services.price.ExchangeRateListener;
import billiongoods.server.services.price.MarkupType;
import billiongoods.server.services.price.PriceConverter;
import billiongoods.server.warehouse.Price;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernatePriceConverter implements PriceConverter, InitializingBean {
	private double exchangeRate;

	private SessionFactory sessionFactory;

	private final Collection<ExchangeRateListener> listeners = new CopyOnWriteArraySet<>();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.price.PriceConverter");

	public HibernatePriceConverter() {
	}

	public HibernatePriceConverter(double exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	@Override
	public void addExchangeRateListener(ExchangeRateListener l) {
		if (l != null) {
			listeners.add(l);
		}
	}

	@Override
	public void removeExchangeRateListener(ExchangeRateListener l) {
		listeners.remove(l);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final Session session = sessionFactory.openSession();

		final Query query = session.createQuery("from billiongoods.server.services.price.impl.HibernateExchangeRate order by timestamp desc");
		query.setMaxResults(1);

		final HibernateExchangeRate rate = (HibernateExchangeRate) query.uniqueResult();
		if (rate == null) {
			exchangeRate = 35f;
			log.info("Exchange rate set to predefined: {}", exchangeRate);
		} else {
			exchangeRate = rate.getExchangeRate();
			log.info("Exchange rate load from DB: {}", exchangeRate);
		}
		session.flush();
		session.close();
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

		double old = this.exchangeRate;

		this.exchangeRate = exchangeRate;

		for (ExchangeRateListener listener : listeners) {
			listener.exchangeRateUpdated(old, exchangeRate);
		}
	}


	@Override
	public String formula(String name, String roundFunction, MarkupType markup) {
		return roundFunction + "(((" + name + " + (" + name + "*" + (markup.getMarkupPercents() / 100.) + "))*" + exchangeRate + " + " + markup.getMarkupFixed() + ")*100)/100";
	}

	@Override
	public Price convert(Price p, MarkupType markup) {
		double a = convert(p.getAmount(), exchangeRate, markup);
		Double pa = p.getPrimordialAmount() == null ? null : convert(p.getPrimordialAmount(), exchangeRate, markup);
		return new Price(a, pa);
	}

	protected double convert(double amount, double exchangeRate, MarkupType markup) {
		return Price.round((amount + amount * markup.getMarkupPercents() / 100.) * exchangeRate + markup.getMarkupFixed());
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
