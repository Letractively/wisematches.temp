package billiongoods.server.services.payment;

import billiongoods.server.warehouse.ProductItem;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderItem extends ProductItem {
	Integer getNumber();

	double getWeight();

	String getOptions();
}