package billiongoods.server.services.basket;

import billiongoods.server.warehouse.ProductDescription;
import billiongoods.server.warehouse.Property;

import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface BasketItem {
	int getNumber();

	int getQuantity();

	double getAmount();

	ProductDescription getProduct();

	Collection<Property> getOptions();
}
