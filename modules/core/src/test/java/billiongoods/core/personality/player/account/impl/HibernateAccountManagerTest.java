package billiongoods.core.personality.player.account.impl;

import billiongoods.core.Language;
import billiongoods.core.personality.player.account.*;
import org.easymock.EasyMock;
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
            createAccount("pwd");
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

        final Account player1 = createAccount("pwd");
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
    public void testDuplicateUsername() throws Exception {
        final Account account = createAccount("pwd");

        final AccountEditor editor = createMockEditor();
        editor.setNickname(account.getNickname());
        try {
            accountManager.createAccount(editor.createAccount(), "pwd");
            fail("DuplicateAccountException must be here");
        } catch (DuplicateAccountException ex) {
            assertEquals(1, ex.getFieldNames().size());
            assertTrue(ex.getFieldNames().contains("username"));
        }

        final AccountEditor editor2 = createMockEditor();
        editor2.setNickname(account.getNickname().toUpperCase());
        try {
            accountManager.createAccount(editor2.createAccount(), "pwd");
            fail("DuplicateAccountException must be here");
        } catch (DuplicateAccountException ex) {
            assertEquals(1, ex.getFieldNames().size());
            assertTrue(ex.getFieldNames().contains("username"));
        }
    }

    @Test
    public void testDuplicateEMail() throws Exception {
        final Account account = createAccount("pwd");

        final AccountEditor editor = createMockEditor();
        editor.setEmail(account.getEmail());
        try {
            accountManager.createAccount(editor.createAccount(), "pwd");
            fail("DuplicateAccountException must be here");
        } catch (DuplicateAccountException ex) {
            assertEquals(1, ex.getFieldNames().size());
            assertTrue(ex.getFieldNames().contains("email"));
        }

        final AccountEditor editor2 = createMockEditor();
        editor2.setEmail(account.getEmail().toUpperCase());
        try {
            accountManager.createAccount(editor2.createAccount(), "pwd");
            fail("DuplicateAccountException must be here");
        } catch (DuplicateAccountException ex) {
            assertEquals(1, ex.getFieldNames().size());
            assertTrue(ex.getFieldNames().contains("email"));
        }
    }

    @Test
    public void testDuplicateBoth() throws Exception {
        final Account account = createAccount("pwd");

        final AccountEditor editor = createMockEditor();
        editor.setNickname(account.getNickname());
        editor.setEmail(account.getEmail());
        try {
            accountManager.createAccount(editor.createAccount(), "pwd");
            fail("DuplicateAccountException must be here");
        } catch (DuplicateAccountException ex) {
            assertEquals(2, ex.getFieldNames().size());
            assertTrue(ex.getFieldNames().contains("username"));
            assertTrue(ex.getFieldNames().contains("email"));
        }

        final AccountEditor editor2 = createMockEditor();
        editor2.setNickname(account.getNickname().toUpperCase());
        editor2.setEmail(account.getEmail().toUpperCase());
        try {
            accountManager.createAccount(editor2.createAccount(), "pwd");
            fail("DuplicateAccountException must be here");
        } catch (DuplicateAccountException ex) {
            assertEquals(2, ex.getFieldNames().size());
            assertTrue(ex.getFieldNames().contains("username"));
            assertTrue(ex.getFieldNames().contains("email"));
        }
    }

    @Test
    public void testUpdateAccount() throws Exception {
        final AccountListener l = EasyMock.createStrictMock(AccountListener.class);
        l.accountUpdated(EasyMock.isA(Account.class), EasyMock.isA(HibernateAccount.class));
        EasyMock.replay(l);

        final Account p = createAccount("pwd");

        final AccountEditor e = new AccountEditor(p);
        e.setEmail("modified_" + e.getEmail());
        e.setLanguage(Language.RU);
        e.setTimeZone(TimeZone.getTimeZone("GMT+04:12"));

        accountManager.addAccountListener(l);
        try {
            accountManager.updateAccount(e.createAccount(), "pwd2");
            EasyMock.verify(l);
        } finally {
            accountManager.removeAccountListener(l);
        }

        final Account player = accountManager.getAccount(p.getId());
        assertEquals(e.getEmail(), player.getEmail());
        assertEquals(e.getNickname(), player.getNickname());
        assertEquals(e.getLanguage(), player.getLanguage());
        assertEquals(e.getTimeZone(), player.getTimeZone());
        assertEquals(TimeZone.getTimeZone("GMT+04:12"), player.getTimeZone());
    }

    @Test
    public void testValidatePassword() throws DuplicateAccountException, InadmissibleUsernameException, UnknownAccountException {
        final Account op = createMockEditor().createAccount();

        final Account mock = accountManager.createAccount(op, "mockPwd");
        assertTrue(accountManager.checkAccountCredentials(mock.getId(), "mockPwd"));
        assertFalse(accountManager.checkAccountCredentials(mock.getId(), "mockPwd2"));

        final Account account = accountManager.updateAccount(mock, "mockPwd3");
        assertTrue(accountManager.checkAccountCredentials(account.getId(), "mockPwd3"));
        assertFalse(accountManager.checkAccountCredentials(account.getId(), "mockPwd"));

        accountManager.removeAccount(account);
    }

    private Account createAccount(final String pwd) throws Exception {
        final Account op = createMockEditor().createAccount();
        final Account mock = accountManager.createAccount(op, pwd);

        assertNotNull(mock);
        assertFalse(0 == mock.getId());
        assertEquals(op.getEmail(), mock.getEmail());
        assertEquals(op.getNickname(), mock.getNickname());
        assertEquals(Language.DEFAULT, mock.getLanguage());
        assertEquals(TimeZone.getDefault(), mock.getTimeZone());
        return mock;
    }

    private AccountEditor createMockEditor() {
        final String id = UUID.randomUUID().toString();
        return new AccountEditor(id + "@wm.net", id);
    }
}
