package billiongoods.server.services.address.impl;

import billiongoods.core.Personality;
import billiongoods.server.services.address.AddressBook;
import billiongoods.server.services.address.AddressRecord;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "privacy_address_book")
public class HibernateAddressBook implements AddressBook {
	@Id
	@Column(name = "id", nullable = false, updatable = false, unique = true)
	private Long id;

	@Column(name = "primaryAddress")
	private Integer primary;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "addressBook", targetEntity = HibernateAddressRecord.class)
	private List<AddressRecord> addressRecords = new ArrayList<>();

	HibernateAddressBook() {
	}

	public HibernateAddressBook(Personality personality) {
		this.id = personality.getId();
	}

	public Long getId() {
		return id;
	}

	@Override
	public AddressRecord getPrimary() {
		return getAddressRecord(primary);
	}

	@Override
	public List<AddressRecord> getAddresses() {
		return addressRecords;
	}

	@Override
	public HibernateAddressRecord getAddressRecord(Integer id) {
		if (id == null) {
			return null;
		}
		for (AddressRecord addressRecord : addressRecords) {
			if (addressRecord.getId().equals(id)) {
				return (HibernateAddressRecord) addressRecord;
			}
		}
		return null;
	}

	void addAddressRecord(HibernateAddressRecord record) {
		if (addressRecords.add(record) && primary == null) {
			primary = record.getId();
		}
	}

	void removeAddressRecord(HibernateAddressRecord record) {
		if (addressRecords.remove(record)) {
			if (record.getId().equals(primary)) {
				if (addressRecords.size() > 0) {
					primary = addressRecords.get(0).getId();
				} else {
					primary = null;
				}
			}
		}
	}

	boolean setPrimary(AddressRecord address) {
		if (addressRecords.contains(address)) {
			this.primary = address.getId();
			return true;
		}
		return false;
	}

	void replaceAddressBook(HibernateAddressRecord oldAddressRecord, HibernateAddressRecord newAddressRecord) {
		addressRecords.remove(oldAddressRecord);
		addressRecords.add(newAddressRecord);

		if (oldAddressRecord.getId().equals(primary)) {
			primary = newAddressRecord.getId();
		}
	}
}
