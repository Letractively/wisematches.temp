package billiongoods.server.web.servlet.mvc.privacy.form;

import billiongoods.server.services.address.AddressRecord;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AddressForm {
	private Integer id;
	private String firstName;
	private String lastName;
	private String postcode;
	private String region;
	private String city;
	private String location;

	private static final CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();

	public AddressForm() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public AddressRecord toAddressRecord() {
		return new AddressRecord(firstName, lastName, postcode, region, city, location);
	}

	public Map<String, String> validate() {
		final Map<String, String> res = new HashMap<>(7);
		checkAddressField("firstName", firstName, res);
		checkAddressField("lastName", lastName, res);
		checkAddressField("location", location, res);
		checkAddressField("region", region, res);
		checkAddressField("city", city, res);
		checkAddressField("postcode", postcode, res);

		if (!postcode.matches("\\d{6}+")) {
			res.put("postcode", "address.err.postcode.format");
		}

		return res;
	}

	private void checkAddressField(String name, String value, Map<String, String> errors) {
		if (value == null || value.trim().isEmpty()) {
			errors.put(name, "address.err." + name + ".empty");
		} else if (!asciiEncoder.canEncode(value)) {
			errors.put(name, "address.err.ascii");
		}
	}
}
