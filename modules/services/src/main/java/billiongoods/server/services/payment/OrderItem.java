package billiongoods.server.services.payment;

import billiongoods.server.warehouse.ProductPreview;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderItem {
	Integer getNumber();

	int getQuantity();

	double getAmount();

	double getWeight();

	String getOptions();

	ProductPreview getProduct();
}