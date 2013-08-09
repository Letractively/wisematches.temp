package billiongoods.server.services.paypal;

import billiongoods.server.services.payment.Order;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface WebAddressResolver {
	String getReturnURL();

	String getCancelURL();


	String getOrderURL(Order order);

	String getArticleURL(Integer number);
}
