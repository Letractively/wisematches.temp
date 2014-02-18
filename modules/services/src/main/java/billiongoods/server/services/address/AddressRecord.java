package billiongoods.server.services.address;

import javax.persistence.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
@MappedSuperclass
public class AddressRecord extends Address {
	@Id
	@Column(name = "id", nullable = false, updatable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	protected AddressRecord() {
	}

	public AddressRecord(AddressRecord address) {
		super(address);
	}

	public AddressRecord(String firstName, String lastName, String phone, String postcode, String region, String city, String location) {
		super(firstName, lastName, phone, postcode, region, city, location);
	}

	public Integer getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AddressRecord)) return false;

		AddressRecord that = (AddressRecord) o;
		return !(id != null ? !id.equals(that.id) : that.id != null);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
