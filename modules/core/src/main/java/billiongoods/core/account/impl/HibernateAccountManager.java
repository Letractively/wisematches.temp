package billiongoods.core.account.impl;

import billiongoods.core.account.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateAccountManager implements AccountManager {
    private SessionFactory sessionFactory;
    private AccountLockManager accountLockManager;
    private PasswordEncoder passwordEncoder = new StandardPasswordEncoder();

    private final Collection<AccountListener> accountListeners = new CopyOnWriteArraySet<>();

    private static final String CHECK_ACCOUNT_AVAILABILITY = "" +
            "select account.username, account.email " +
            "from HibernateAccount as account " +
            "where account.username like :nick or account.email like :email";

    private final Lock lock = new ReentrantLock();

    public HibernateAccountManager() {
    }

    @Override
    public void addAccountListener(AccountListener l) {
        if (l != null) {
            accountListeners.add(l);
        }
    }

    @Override
    public void removeAccountListener(AccountListener l) {
        if (l != null) {
            accountListeners.remove(l);
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_UNCOMMITTED)
    public Account getAccount(Long playerId) {
        lock.lock();
        try {
            return (HibernateAccount) sessionFactory.getCurrentSession().get(HibernateAccount.class, playerId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_UNCOMMITTED)
    public Account findByEmail(String email) {
        lock.lock();
        try {
            final Session session = sessionFactory.getCurrentSession();
            final Query query = session.createQuery("from HibernateAccount user where user.email=:email");
            query.setString("email", email);
            final List l = query.list();
            if (l.size() != 1) {
                return null;
            }
            return (Account) l.get(0);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_UNCOMMITTED)
    public Account createAccount(Account account, String password) throws DuplicateAccountException, InadmissibleUsernameException {
        lock.lock();
        try {
            checkAccount(account);

            final Session session = sessionFactory.getCurrentSession();
            final HibernateAccount hp = new HibernateAccount(account, passwordEncoder.encode(password));
            session.save(hp);
            for (AccountListener accountListener : accountListeners) {
                accountListener.accountCreated(hp);
            }
            return hp;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_UNCOMMITTED)
    public Account updateAccount(Account account, String password) throws UnknownAccountException, DuplicateAccountException, InadmissibleUsernameException {
        lock.lock();
        try {
            HibernateAccount oldAccount = (HibernateAccount) getAccount(account.getId());
            if (oldAccount == null) {
                throw new UnknownAccountException(account);
            }

            if (!oldAccount.getEmail().equalsIgnoreCase(account.getEmail())) {
                if (!checkAccountAvailable(account.getUsername(), account.getEmail()).isEmailAvailable()) {
                    throw new DuplicateAccountException(account, "email");
                }
            }

            // Copy previous state
            final Account prev = new AccountEditor(oldAccount).createAccount();

            String pwd = password;
            if (pwd != null) {
                pwd = passwordEncoder.encode(password);
            }

            // merge and update
            final Session session = sessionFactory.getCurrentSession();
            oldAccount.updateAccountInfo(account, pwd);
            session.save(oldAccount);

            for (AccountListener playerListener : accountListeners) {
                playerListener.accountUpdated(prev, oldAccount);
            }
            return oldAccount;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_UNCOMMITTED)
    public Account removeAccount(Account account) throws UnknownAccountException {
        lock.lock();
        try {
            final HibernateAccount hp = (HibernateAccount) getAccount(account.getId());
            if (hp != null) {
                sessionFactory.getCurrentSession().delete(hp);

                for (AccountListener accountListener : accountListeners) {
                    accountListener.accountRemove(account);
                }
                return hp;
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_UNCOMMITTED)
    public boolean checkAccountCredentials(Long id, String password) {
        lock.lock();
        try {
            final Session session = sessionFactory.getCurrentSession();
            final Query query = session.createQuery("select password, username from HibernateAccount where id=:pid");
            query.setParameter("pid", id);

            final Object[] o = (Object[]) query.uniqueResult();
            return o != null && passwordEncoder.matches(password, (String) o[0]);
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, isolation = Isolation.READ_UNCOMMITTED)
    public AccountAvailability checkAccountAvailable(final String username, final String email) {
        lock.lock();
        try {
            final Session session = sessionFactory.getCurrentSession();
            final Query query = session.createQuery(CHECK_ACCOUNT_AVAILABILITY);
            query.setString("nick", username);
            query.setString("email", email);

            final long[] res = new long[2];
            final List list = query.list();
            for (Object lValue : list) {
                final Object[] o = (Object[]) lValue;
                if (username.equalsIgnoreCase(String.valueOf(o[0]))) {
                    res[0]++;
                }
                if (email.equalsIgnoreCase(String.valueOf(o[1]))) {
                    res[1]++;
                }
            }
            return new AccountAvailability(res[1] == 0, res[0] == 0, accountLockManager.isNicknameLocked(username) == null);
        } finally {
            lock.unlock();
        }
    }

    private void checkAccount(final Account account) throws InadmissibleUsernameException, DuplicateAccountException {
        final String reason = accountLockManager.isNicknameLocked(account.getUsername());
        if (reason != null) {
            throw new InadmissibleUsernameException(account, reason);
        }

        final AccountAvailability a = checkAccountAvailable(account.getUsername(), account.getEmail());
        if (!a.isAvailable()) {
            if (!a.isEmailAvailable() && a.isUsernameAvailable()) {
                throw new DuplicateAccountException(account, "email");
            } else if (a.isEmailAvailable() && !a.isUsernameAvailable()) {
                throw new DuplicateAccountException(account, "username");
            } else {
                throw new DuplicateAccountException(account, "username", "email");
            }
        }
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setAccountLockManager(AccountLockManager accountLockManager) {
        this.accountLockManager = accountLockManager;
    }
}
