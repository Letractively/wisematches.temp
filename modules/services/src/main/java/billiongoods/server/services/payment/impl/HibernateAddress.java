package billiongoods.server.services.payment.impl;

import billiongoods.server.services.payment.Address;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class HibernateAddress implements Address {
	@Column(name = "addressName")
	private String name;

	@Column(name = "addressRegion")
	private String region;

	@Column(name = "addressCity")
	private String city;

	@Column(name = "addressPostal")
	private String postalCode;

	@Column(name = "addressStreet")
	private String streetAddress;

	public HibernateAddress() {
	}

	public HibernateAddress(Address address) {
		this.name = address.getName();
		this.region = address.getRegion();
		this.city = address.getCity();
		this.postalCode = address.getPostalCode();
		this.streetAddress = address.getStreetAddress();
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	@Override
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Override
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Address{");
		sb.append("streetAddress='").append(streetAddress).append('\'');
		sb.append(", city='").append(city).append('\'');
		sb.append(", region='").append(region).append('\'');
		sb.append(", postalCode='").append(postalCode).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
