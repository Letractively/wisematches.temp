package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderItem {
	String getName();

	Integer getArticle();


	int getQuantity();

	float getAmount();


	float getWeight();

	String getOptions();
}
