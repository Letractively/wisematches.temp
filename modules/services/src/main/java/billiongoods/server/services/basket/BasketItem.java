package billiongoods.server.services.basket;

import billiongoods.server.warehouse.Property;

import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface BasketItem {
	int getQuantity();

	Integer getArticleId();

	Collection<Property> getProperties();
}
