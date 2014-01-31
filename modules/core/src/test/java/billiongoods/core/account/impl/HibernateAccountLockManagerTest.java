package billiongoods.core.account.impl;

import billiongoods.core.Passport;
import billiongoods.core.account.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/properties-config.xml",
		"classpath:/config/database-config.xml",
		"classpath:/config/personality-config.xml"
})
public class HibernateAccountLockManagerTest {
	@Autowired
	private AccountLockManager accountLockManager;

	@Autowired
	private AccountManager accountManager;

	private Account player;

	private AccountLockListener accountLockListener;
	private AccountNicknameLockListener listenerAccountNickname;

	public HibernateAccountLockManagerTest() {
	}

	@Before
	public void createAccount() throws Exception {
		UUID uuid = UUID.randomUUID();
		player = accountManager.createAccount(uuid + "@qwe.ru", uuid.toString(), new Passport("asd"));
	}

	@After
	public void removeAccount() throws Exception {
		accountManager.removeAccount(player);
	}

	@BeforeTransaction
	public void onSetUp() throws Exception {
		accountLockListener = createStrictMock(AccountLockListener.class);
		listenerAccountNickname = createStrictMock(AccountNicknameLockListener.class);

		accountLockManager.addAccountLockListener(accountLockListener);
		accountLockManager.addAccountNicknameLockListener(listenerAccountNickname);
	}

	@AfterTransaction
	public void onTearDown() throws Exception {
		accountLockManager.removeAccountLockListener(accountLockListener);
		accountLockManager.removeAccountNicknameLockListener(listenerAccountNickname);
	}

	@Test
	public void testLockUnlockAccount() {
		final Date unlockDate = new Date(new Date().getTime() + 10000);

		reset(accountLockListener);
		accountLockListener.accountLocked(player, "You are locked", "Fuck off", unlockDate);
		replay(accountLockListener);

		assertFalse(accountLockManager.isAccountLocked(player));
		accountLockManager.lockAccount(player, "You are locked", "Fuck off", unlockDate);
		assertTrue(accountLockManager.isAccountLocked(player));
		verify(accountLockListener);

		reset(accountLockListener);
		accountLockListener.accountUnlocked(player);
		replay(accountLockListener);

		accountLockManager.unlockAccount(player);
		assertFalse(accountLockManager.isAccountLocked(player));
		verify(accountLockListener);
	}

	@Test
	public void testIsAccountLocked() throws InterruptedException {
		final Date unlockDate = new Date(System.currentTimeMillis() + 1000);

		reset(accountLockListener);
		accountLockListener.accountLocked(player, "t", "t", unlockDate);
		accountLockListener.accountUnlocked(player);
		replay(accountLockListener);

		accountLockManager.lockAccount(player, "t", "t", unlockDate);
		assertTrue(accountLockManager.isAccountLocked(player));

		//Now wait while lock timeout expired
		Thread.sleep(1500);
		assertFalse(accountLockManager.isAccountLocked(player));
	}

	@Test
	public void testGetLockAccountInfo() throws InterruptedException {
		final Date lockDate = new Date((System.currentTimeMillis() / 1000) * 1000);
		final Date unlockDate = new Date(lockDate.getTime() + 1000);

		reset(accountLockListener);
		accountLockListener.accountLocked(player, "t1", "t2", unlockDate);
		accountLockListener.accountUnlocked(player);
		replay(accountLockListener);

		accountLockManager.lockAccount(player, "t1", "t2", unlockDate);

		final AccountLockInfo lockInfo = accountLockManager.getAccountLockInfo(player);
		assertEquals(player, lockInfo.getAccount());
		assertEquals("t1", lockInfo.getPublicReason());
		assertEquals("t2", lockInfo.getPrivateReason());
		assertTrue(Math.abs(lockDate.getTime() - lockInfo.getLockDate().getTime()) < 2000);
		assertEquals(unlockDate, lockInfo.getUnlockDate());

		//Now wait while lock timeout expired
		Thread.sleep(1500);
		assertNull(accountLockManager.getAccountLockInfo(player));
	}

	@Test
	public void test_username() {
		listenerAccountNickname.usernameLocked("test", "test reason");
		listenerAccountNickname.usernameLocked("test", "test reason2");
		listenerAccountNickname.usernameUnlocked("test");
		replay(listenerAccountNickname);

		accountLockManager.addAccountNicknameLockListener(listenerAccountNickname);

		assertNull(accountLockManager.isNicknameLocked("test"));

		accountLockManager.lockNickname("test", "test reason");
		assertEquals("test reason", accountLockManager.isNicknameLocked("test"));

		accountLockManager.lockNickname("test", "test reason2");
		assertEquals("test reason2", accountLockManager.isNicknameLocked("test"));

		accountLockManager.unlockNickname("test2");
		assertEquals("test reason2", accountLockManager.isNicknameLocked("test"));

		accountLockManager.unlockNickname("test");
		assertNotNull(accountLockManager.isNicknameLocked("test"));

		accountLockManager.unlockNickname("test");
		assertNotNull(accountLockManager.isNicknameLocked("test"));

		verify(listenerAccountNickname);
	}
}
