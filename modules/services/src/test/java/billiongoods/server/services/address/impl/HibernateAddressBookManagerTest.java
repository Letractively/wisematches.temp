package billiongoods.server.services.address.impl;

import billiongoods.core.Member;
import billiongoods.core.Passport;
import billiongoods.core.account.*;
import billiongoods.core.account.impl.HibernateAccountManager;
import billiongoods.server.services.address.AddressBook;
import billiongoods.server.services.address.AddressRecord;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/properties-config.xml",
		"classpath:/config/database-config.xml"
})
public class HibernateAddressBookManagerTest {
	@Autowired
	private SessionFactory sessionFactory;

	public HibernateAddressBookManagerTest() {
	}

	@Test
	public void test() throws DuplicateAccountException, InadmissibleUsernameException, UnknownAccountException {
		final AccountLockManager lockManager = createNiceMock(AccountLockManager.class);
		replay(lockManager);

		final HibernateAccountManager accountManager = new HibernateAccountManager();
		accountManager.setSessionFactory(sessionFactory);
		accountManager.setPasswordEncoder(new StandardPasswordEncoder());
		accountManager.setAccountLockManager(lockManager);

		final HibernateAddressBookManager manager = new HibernateAddressBookManager();
		manager.setSessionFactory(sessionFactory);

		final Account account = accountManager.createAccount("addressBook@junit.org", "mock", new Passport("mock"));
		final Member personality = new Member(account.getId(), account.getEmail(), account.getPassport());
		try {
			AddressBook book = manager.getAddressBook(personality);
			assertNotNull(book);
			assertNull(book.getPrimary());
			assertEquals(0, book.getAddresses().size());

			final AddressRecord r1 = manager.addAddress(personality, createMock(1));
			book = manager.getAddressBook(personality);
			assertEquals(1, book.getAddresses().size());
			assertSame(r1, book.getPrimary());

			final AddressRecord r2 = manager.addAddress(personality, createMock(2));
			book = manager.getAddressBook(personality);
			assertEquals(2, book.getAddresses().size());
			assertSame(r1, book.getPrimary());

			final AddressRecord r3 = manager.makePrimary(personality, r2);
			book = manager.getAddressBook(personality);
			assertEquals(2, book.getAddresses().size());
			assertSame(r2, book.getPrimary());

			final AddressRecord r4 = manager.removeAddress(personality, r2);
			book = manager.getAddressBook(personality);
			assertEquals(1, book.getAddresses().size());
			assertSame(r1, book.getPrimary());
		} finally {
			accountManager.removeAccount(account);
		}
	}

	private AddressRecord createMock(int number) {
		return new AddressRecord("firstName_" + number, "lastName_" + number, "post_" + number, "region_" + number, "city_" + number, "street_" + number);
	}
}