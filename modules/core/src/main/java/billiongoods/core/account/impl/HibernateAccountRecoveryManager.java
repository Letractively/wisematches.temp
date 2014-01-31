package billiongoods.core.account.impl;

import billiongoods.core.account.Account;
import billiongoods.core.account.AccountRecoveryManager;
import billiongoods.core.account.RecoveryToken;
import billiongoods.core.task.CleaningDayListener;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateAccountRecoveryManager implements AccountRecoveryManager, CleaningDayListener {
	private SessionFactory sessionFactory;

	private long tokenExpirationTime = DEFAULT_EXPIRATION_TIME;

	private static final int DEFAULT_EXPIRATION_TIME = 24 * 60 * 60 * 1000;  // 1day

	private static final Logger log = LoggerFactory.getLogger("billiongoods.account.RecoveryManager");

	public HibernateAccountRecoveryManager() {
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public RecoveryToken generateToken(Account account) {
		clearToken(account); // clear previous token

		final Session session = sessionFactory.getCurrentSession();
		final RecoveryToken token = new HibernateRecoveryToken(account, generateToken(), new Date());
		session.save(token);
		return token;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public RecoveryToken getToken(Account account) {
		final Session session = sessionFactory.getCurrentSession();
		final RecoveryToken token = (RecoveryToken) session.get(HibernateRecoveryToken.class, account.getId());
		if (token == null) {
			return null;
		}

		// check that token still alive
		if (token.getGenerated().getTime() + tokenExpirationTime < System.currentTimeMillis()) {
			return null;
		}
		return token;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public RecoveryToken clearToken(Account account) {
		final Session session = sessionFactory.getCurrentSession();
		final RecoveryToken token = (RecoveryToken) session.get(HibernateRecoveryToken.class, account.getId());
		if (token != null) {
			session.delete(token);
		}
		return token;
	}

	@Override
	public void cleanup(Date today) {
		try {
			final Session session = sessionFactory.getCurrentSession();
			final Query query = session.createQuery("delete from HibernateRecoveryToken where generated<:date");
			final Date val = new Date(today.getTime() - tokenExpirationTime);
			query.setParameter("date", val);
			final int i = query.executeUpdate();
			log.info("Expired recovery tokens were cleaned: {}", i);
		} catch (HibernateException ex) {
			log.error("Expired recovery tokens can't be cleaned ", ex);
		}
	}

	private String generateToken() {
		final StringBuilder b = new StringBuilder();
		while (b.length() < 10) {
			b.append((int) (Math.random() * 9));
		}
		return b.toString();
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setTokenExpirationTime(long tokenExpirationTime) {
		this.tokenExpirationTime = tokenExpirationTime;
	}
}
