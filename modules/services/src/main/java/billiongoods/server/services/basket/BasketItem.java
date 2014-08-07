package billiongoods.server.services.basket;

import billiongoods.server.warehouse.ProductItem;
import billiongoods.server.warehouse.Property;

import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface BasketItem extends ProductItem {
	int getNumber();

	Collection<Property> getOptions();
}
