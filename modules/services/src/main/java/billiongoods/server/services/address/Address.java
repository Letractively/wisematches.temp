package billiongoods.server.services.address;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
@MappedSuperclass
public class Address {
	@Column(name = "firstName", length = 145, nullable = false)
	private String firstName;

	@Column(name = "lastName", length = 245, nullable = false)
	private String lastName;

	@Column(name = "postcode", length = 10, nullable = false)
	private String postcode;

	@Column(name = "region", length = 145, nullable = false)
	private String region;

	@Column(name = "city", length = 145, nullable = false)
	private String city;

	@Column(name = "location", length = 250, nullable = false)
	private String location;

	protected Address() {
	}

	public Address(Address address) {
		this(address.firstName, address.lastName, address.postcode, address.region, address.city, address.location);
	}

	public Address(String firstName, String lastName, String postcode, String region, String city, String location) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.postcode = postcode;
		this.region = region;
		this.city = city;
		this.location = location;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPostcode() {
		return postcode;
	}

	public String getRegion() {
		return region;
	}

	public String getCity() {
		return city;
	}

	public String getLocation() {
		return location;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Address{");
		sb.append("firstName='").append(firstName).append('\'');
		sb.append(", lastName='").append(lastName).append('\'');
		sb.append(", postcode='").append(postcode).append('\'');
		sb.append(", region='").append(region).append('\'');
		sb.append(", city='").append(city).append('\'');
		sb.append(", location='").append(location).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
