package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Address {
	String getName();

	String getCity();

	String getRegion();

	String getPostalCode();

	String getStreetAddress();
}
