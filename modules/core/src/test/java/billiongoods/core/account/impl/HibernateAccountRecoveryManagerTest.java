package billiongoods.core.account.impl;

import billiongoods.core.Passport;
import billiongoods.core.account.*;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/properties-config.xml",
		"classpath:/config/database-config.xml",
		"classpath:/config/personality-config.xml"
})
public class HibernateAccountRecoveryManagerTest {
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private AccountManager accountManager;

	private HibernateAccountRecoveryManager recoveryTokenManager;

	public HibernateAccountRecoveryManagerTest() {
	}

	@Before
	public void setUp() throws Exception {
		recoveryTokenManager = new HibernateAccountRecoveryManager();
		recoveryTokenManager.setSessionFactory(sessionFactory);
	}

	@Test
	public void testManager() throws InterruptedException, InadmissibleUsernameException, DuplicateAccountException, UnknownAccountException {
		final Account p = accountManager.createAccount("mock@mock.bg", "mock", new Passport("mock"));

		final RecoveryToken token = recoveryTokenManager.generateToken(p);
		assertNotNull(token);
		assertNotNull(token.getToken());
		assertNotNull(token.getGenerated());

		Thread.sleep(100);
		assertNotSame(token, recoveryTokenManager.generateToken(p));

		final RecoveryToken token1 = recoveryTokenManager.getToken(p);
		assertFalse(token.getToken().equals(token1.getToken()));
		assertFalse(token.getGenerated().equals(token1.getGenerated()));

		recoveryTokenManager.setTokenExpirationTime(300);
		assertNotNull(recoveryTokenManager.getToken(p));

		Thread.sleep(400);
		assertNull(recoveryTokenManager.getToken(p));

		final RecoveryToken token2 = recoveryTokenManager.generateToken(p);
		assertNotNull(token2);
		assertEquals(token2, recoveryTokenManager.clearToken(p));
		assertNull(recoveryTokenManager.getToken(p));

		final RecoveryToken token3 = recoveryTokenManager.generateToken(p);
		assertNotNull(token3);
		recoveryTokenManager.cleanup(new Date(System.currentTimeMillis() + 1401));
		sessionFactory.getCurrentSession().clear(); // required for "delete" operation to clear internal cache
		assertNull(recoveryTokenManager.getToken(p));

		accountManager.removeAccount(p);
	}
}
