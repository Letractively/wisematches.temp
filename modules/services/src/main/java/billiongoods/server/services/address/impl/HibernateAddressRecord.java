package billiongoods.server.services.address.impl;

import billiongoods.server.services.address.AddressRecord;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "privacy_address_record")
public class HibernateAddressRecord extends AddressRecord {
	@Column(name = "addressBook")
	private Long addressBook;

	HibernateAddressRecord() {
	}

	public HibernateAddressRecord(HibernateAddressBook book, AddressRecord address) {
		super(address);
		this.addressBook = book.getId();
	}
}
