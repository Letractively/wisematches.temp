package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.server.services.payment.Address;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderConfirmForm implements Address {
	private int[] itemQuantities;
	private Integer[] itemNumbers;

	private String name;
	private String region;
	private String locality;
	private String postalCode;
	private String streetAddress;

	public OrderConfirmForm() {
	}

	public Integer[] getItemNumbers() {
		return itemNumbers;
	}

	public void setItemNumbers(Integer[] itemNumbers) {
		this.itemNumbers = itemNumbers;
	}

	public int[] getItemQuantities() {
		return itemQuantities;
	}

	public void setItemQuantities(int[] itemQuantities) {
		this.itemQuantities = itemQuantities;
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
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
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

	public void setCountry(String country) {
	}
}
