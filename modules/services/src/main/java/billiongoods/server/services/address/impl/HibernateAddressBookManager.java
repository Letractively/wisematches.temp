package billiongoods.server.services.address.impl;

import billiongoods.core.Personality;
import billiongoods.server.services.address.AddressBook;
import billiongoods.server.services.address.AddressBookManager;
import billiongoods.server.services.address.AddressRecord;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateAddressBookManager implements AddressBookManager {
	private SessionFactory sessionFactory;

	private static final AddressBook EMPTY_BOOK = new HibernateAddressBook();

	public HibernateAddressBookManager() {
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public AddressRecord addAddress(Personality personality, AddressRecord address) {
		final Session session = sessionFactory.getCurrentSession();

		HibernateAddressBook book = (HibernateAddressBook) session.get(HibernateAddressBook.class, personality.getId());
		if (book == null) {
			book = new HibernateAddressBook(personality);
			session.save(book);
		}

		final HibernateAddressRecord record = new HibernateAddressRecord(book, address);
		session.save(record);

		book.addAddressRecord(record);
		session.update(book);

		return record;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public AddressRecord makePrimary(Personality personality, AddressRecord address) {
		final Session session = sessionFactory.getCurrentSession();

		HibernateAddressBook book = (HibernateAddressBook) session.get(HibernateAddressBook.class, personality.getId());
		if (book == null) {
			return null;
		}

		if (book.setPrimary(address)) {
			session.update(book);
		}
		return address;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public AddressRecord removeAddress(Personality personality, AddressRecord address) {
		final Session session = sessionFactory.getCurrentSession();

		HibernateAddressBook book = (HibernateAddressBook) session.get(HibernateAddressBook.class, personality.getId());
		if (book == null) {
			return null;
		}

		final HibernateAddressRecord addressRecord = book.getAddressRecord(address.getId());
		if (addressRecord == null) {
			return null;
		}

		session.delete(addressRecord);

		book.removeAddressRecord(addressRecord);
		session.update(book);

		return addressRecord;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public AddressRecord updateAddress(Personality personality, AddressRecord from, AddressRecord to) {
		final Session session = sessionFactory.getCurrentSession();

		HibernateAddressBook book = (HibernateAddressBook) session.get(HibernateAddressBook.class, personality.getId());
		if (book == null) {
			return null;
		}

		final HibernateAddressRecord oldAddressRecord = book.getAddressRecord(from.getId());
		if (oldAddressRecord == null) {
			return null;
		}

		final HibernateAddressRecord newAddressRecord = new HibernateAddressRecord(book, to);

		session.delete(oldAddressRecord);
		session.save(newAddressRecord);

		book.replaceAddressBook(oldAddressRecord, newAddressRecord);
		session.update(book);

		return newAddressRecord;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public AddressBook getAddressBook(Personality personality) {
		final Session session = sessionFactory.getCurrentSession();
		final HibernateAddressBook book = (HibernateAddressBook) session.get(HibernateAddressBook.class, personality.getId());
		if (book == null) {
			return EMPTY_BOOK;
		}
		return book;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
