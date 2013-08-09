package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderItem {
	Integer getNumber();


	String getCode();

	String getName();


	int getQuantity();

	float getAmount();


	float getWeight();

	String getOptions();
}
