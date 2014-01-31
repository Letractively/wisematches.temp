package billiongoods.server.services.basket;

import billiongoods.server.warehouse.ProductPreview;
import billiongoods.server.warehouse.Property;

import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface BasketItem {
	int getNumber();

	int getQuantity();

	double getAmount();

	ProductPreview getProduct();

	Collection<Property> getOptions();
}
