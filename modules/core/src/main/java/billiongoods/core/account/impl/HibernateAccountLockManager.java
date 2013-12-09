package billiongoods.core.account.impl;

import billiongoods.core.account.*;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public class HibernateAccountLockManager implements AccountLockManager {
	private SessionFactory sessionFactory;
	private final Collection<AccountLockListener> accountLockListeners = new CopyOnWriteArraySet<AccountLockListener>();
	private final Collection<AccountNicknameLockListener> listenerAccountNicknames = new CopyOnWriteArraySet<AccountNicknameLockListener>();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.account.HibernateLockManager");

	public HibernateAccountLockManager() {
	}

	public void addAccountLockListener(AccountLockListener l) {
		accountLockListeners.add(l);
	}

	public void removeAccountLockListener(AccountLockListener l) {
		accountLockListeners.remove(l);
	}

	public void addAccountNicknameLockListener(AccountNicknameLockListener l) {
		listenerAccountNicknames.add(l);
	}

	public void removeAccountNicknameLockListener(AccountNicknameLockListener l) {
		listenerAccountNicknames.remove(l);
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void lockAccount(Account player, String publicReason, String privateReason, Date unlockDate) {
		final Session session = sessionFactory.getCurrentSession();
		final HibernateAccountLockInfo lai = (HibernateAccountLockInfo) session.get(HibernateAccountLockInfo.class, player.getId());
		if (lai != null) {
			lai.setPublicReason(publicReason);
			lai.setPrivateReason(privateReason);
			lai.setLockDate(new Date());
			lai.setUnlockDate(unlockDate);
			session.update(lai);
		} else {
			session.persist(new HibernateAccountLockInfo(player, publicReason, privateReason, unlockDate));
		}

		for (AccountLockListener accountLockListener : accountLockListeners) {
			accountLockListener.accountLocked(player, publicReason, privateReason, unlockDate);
		}
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void unlockAccount(Account player) {
		final Session session = sessionFactory.getCurrentSession();
		final AccountLockInfo lai = (AccountLockInfo) session.get(HibernateAccountLockInfo.class, player.getId());
		if (lai != null) {
			session.delete(lai);

			for (AccountLockListener accountLockListener : accountLockListeners) {
				accountLockListener.accountUnlocked(player);
			}
		}
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean isAccountLocked(final Account account) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("" +
				"select lock.unlockDate " +
				"from HibernateAccountLockInfo lock " +
				"where lock.accountId = :pid");
		query.setParameter("pid", account.getId());
		final Date unlockDate = (Date) query.uniqueResult();

		if (unlockDate != null) {
			final boolean res = System.currentTimeMillis() < unlockDate.getTime();
			if (!res) {
				final Query q = session.createQuery("delete from HibernateAccountLockInfo " +
						"lock where lock.accountId = :pid");
				q.setParameter("pid", account.getId());
				q.executeUpdate();

				for (AccountLockListener accountLockListener : accountLockListeners) {
					accountLockListener.accountUnlocked(account);
				}
			}
			return res;
		}
		return false;
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AccountLockInfo getAccountLockInfo(Account player) {
		final Session session = sessionFactory.getCurrentSession();
		HibernateAccountLockInfo accountInfo = (HibernateAccountLockInfo) session.get(HibernateAccountLockInfo.class, player.getId());

		log.debug("Get account lock info: {}", accountInfo);
		if (accountInfo != null) {
			//object can be cached so we must update it from database.
			try {
				session.refresh(accountInfo);
			} catch (HibernateException ignore) {
			}

			if (System.currentTimeMillis() < accountInfo.getUnlockDate().getTime()) {
				log.debug("Account is still locked");
				accountInfo.setAccount(player);
			} else {
				log.debug("Account already isn't locked. We can remove lock. Unlock date - {},  current time - ",
						accountInfo.getUnlockDate().getTime(), System.currentTimeMillis());
				session.delete(accountInfo);

				for (AccountLockListener accountLockListener : accountLockListeners) {
					accountLockListener.accountUnlocked(player);
				}
				accountInfo = null;
			}
		}
		return accountInfo;
	}

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public String isNicknameLocked(String nickname) {
		final Session session = sessionFactory.getCurrentSession();
		final AccountNicknameLockInfo name = (AccountNicknameLockInfo) session.get(HibernateAccountNicknameLockInfo.class, nickname);
		if (name != null) {
			return name.getReason();
		}
		return null;
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void lockNickname(String nickname, String reason) {
		final Session session = sessionFactory.getCurrentSession();

		HibernateAccountNicknameLockInfo info = (HibernateAccountNicknameLockInfo) session.get(HibernateAccountNicknameLockInfo.class, nickname);
		if (info == null) {
			info = new HibernateAccountNicknameLockInfo(nickname, reason);
			session.persist(info);
		} else {
			info.setReason(reason);
			session.update(info);
		}

		for (AccountNicknameLockListener listenerAccountNickname : listenerAccountNicknames) {
			listenerAccountNickname.usernameLocked(nickname, reason);
		}
	}

	@Transactional(propagation = Propagation.MANDATORY)
	public void unlockNickname(final String nickname) {
		final Session session = sessionFactory.getCurrentSession();
		Query q = session.createQuery(
				"delete from HibernateAccountNicknameLockInfo where username = :user");
		q.setString("user", nickname);
		final int removedCount = q.executeUpdate();
		if (removedCount != 0) {
			for (AccountNicknameLockListener listenerAccountNickname : listenerAccountNicknames) {
				listenerAccountNickname.usernameUnlocked(nickname);
			}
		}
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
