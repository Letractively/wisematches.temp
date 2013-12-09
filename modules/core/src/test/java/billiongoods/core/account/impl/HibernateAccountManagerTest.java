package billiongoods.core.account.impl;

import billiongoods.core.Language;
import billiongoods.core.Passport;
import billiongoods.core.account.*;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.TimeZone;
import java.util.UUID;

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
public class HibernateAccountManagerTest {
	@Autowired
	private AccountManager accountManager;

	public HibernateAccountManagerTest() {
	}

	@Test
	public void testCreateAccount() throws Exception {
		final AccountListener l = EasyMock.createStrictMock(AccountListener.class);
		l.accountCreated(EasyMock.isA(HibernateAccount.class));
		EasyMock.replay(l);

		accountManager.addAccountListener(l);
		try {
			final String email = generateEmail();
			accountManager.createAccount(email, "pwd", new Passport(email));
			EasyMock.verify(l);
		} finally {
			accountManager.removeAccountListener(l);
		}
	}

	@Test
	public void testRemoveAccount() throws Exception {
		final AccountListener l = EasyMock.createStrictMock(AccountListener.class);
		l.accountRemove(EasyMock.isA(HibernateAccount.class));
		EasyMock.replay(l);

		final String email = generateEmail();
		final Account player1 = accountManager.createAccount(email, "pwd", new Passport(email));
		final Account player2 = accountManager.getAccount(player1.getId());

		assertEquals(player1, player2);
		accountManager.addAccountListener(l);
		try {
			accountManager.removeAccount(player1);
			accountManager.removeAccount(player2);

			assertNull(accountManager.getAccount(player1.getId()));
			EasyMock.verify(l);
		} finally {
			accountManager.removeAccountListener(l);
		}
	}

	@Test
	@Ignore("This functionality is disabled")
	public void testDuplicateUsername() throws Exception {
		final String username = "mock_username";
		accountManager.createAccount(generateEmail(), "pwd", new Passport(username));

		try {
			final String s = generateEmail();
			accountManager.createAccount(s, "pwd", new Passport(username));
			fail("DuplicateAccountException must be here");
		} catch (DuplicateAccountException ex) {
			assertEquals(1, ex.getFieldNames().size());
			assertTrue(ex.getFieldNames().contains("username"));
		}

		try {
			final String s = generateEmail();
			accountManager.createAccount(s, "pwd", new Passport(username));
			fail("DuplicateAccountException must be here");
		} catch (DuplicateAccountException ex) {
			assertEquals(1, ex.getFieldNames().size());
			assertTrue(ex.getFieldNames().contains("username"));
		}
	}

	@Test
	public void testDuplicateEMail() throws Exception {
		final String email = generateEmail();
		accountManager.createAccount(email, "pwd", new Passport(email));

		try {
			accountManager.createAccount(email, "pwd", new Passport(generateEmail()));
			fail("DuplicateAccountException must be here");
		} catch (DuplicateAccountException ex) {
			assertEquals(1, ex.getFieldNames().size());
			assertTrue(ex.getFieldNames().contains("email"));
		}

		try {
			accountManager.createAccount(email, "pwd", new Passport(generateEmail()));
			fail("DuplicateAccountException must be here");
		} catch (DuplicateAccountException ex) {
			assertEquals(1, ex.getFieldNames().size());
			assertTrue(ex.getFieldNames().contains("email"));
		}
	}

	@Test
	public void testUpdateEmail() throws Exception {
		final AccountListener l = EasyMock.createStrictMock(AccountListener.class);
		l.accountUpdated(EasyMock.isA(Account.class), EasyMock.isA(HibernateAccount.class));
		EasyMock.replay(l);

		final String email = generateEmail();
		final Account p = accountManager.createAccount(email, "pwd", new Passport(email));

		accountManager.addAccountListener(l);
		try {
			accountManager.updateEmail(p, "modified_" + email);
			EasyMock.verify(l);
		} finally {
			accountManager.removeAccountListener(l);
		}

		final Account player = accountManager.getAccount(p.getId());
		assertEquals("modified_" + email, player.getEmail());
		assertEquals(email, player.getPassport().getUsername());
	}

	@Test
	public void testUpdatePassport() throws Exception {
		final AccountListener l = EasyMock.createStrictMock(AccountListener.class);
		l.accountUpdated(EasyMock.isA(Account.class), EasyMock.isA(HibernateAccount.class));
		EasyMock.replay(l);

		final String email = generateEmail();
		final Account p = accountManager.createAccount(email, "pwd", new Passport(email));

		final Language lang = Language.EN;
		final TimeZone timeZone = TimeZone.getTimeZone("GMT-8:10");

		accountManager.addAccountListener(l);
		try {
			accountManager.updatePassport(p, new Passport("modified_" + p.getPassport().getUsername(), lang, timeZone));
			EasyMock.verify(l);
		} finally {
			accountManager.removeAccountListener(l);
		}

		final Account player = accountManager.getAccount(p.getId());
		assertEquals(email, player.getEmail());
		assertEquals("modified_" + email, player.getPassport().getUsername());
		assertEquals(lang, player.getPassport().getLanguage());
		assertEquals(timeZone, player.getPassport().getTimeZone());
	}

	@Test
	public void testValidateUpdatePassword() throws DuplicateAccountException, InadmissibleUsernameException, UnknownAccountException {
		final String email = generateEmail();

		final Account mock = accountManager.createAccount(email, "mockPwd", new Passport(email));
		assertTrue(accountManager.validateCredentials(mock.getId(), "mockPwd"));
		assertFalse(accountManager.validateCredentials(mock.getId(), "mockPwd2"));

		final Account account = accountManager.updatePassword(mock, "mockPwd3");
		assertTrue(accountManager.validateCredentials(account.getId(), "mockPwd3"));
		assertFalse(accountManager.validateCredentials(account.getId(), "mockPwd"));

		accountManager.removeAccount(account);
	}

	private String generateEmail() {
		return UUID.randomUUID().toString() + "@mock.bg";
	}
}
