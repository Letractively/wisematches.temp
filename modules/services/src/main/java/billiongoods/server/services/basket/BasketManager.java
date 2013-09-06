package billiongoods.server.services.basket;

import billiongoods.core.Personality;
import billiongoods.server.warehouse.ProductDescription;
import billiongoods.server.warehouse.Property;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface BasketManager {
	Basket getBasket(Personality principal);

	int getBasketSize(Personality principal);


	BasketItem addBasketItem(Personality principal, ProductDescription description, List<Property> options, int quantity);

	BasketItem removeBasketItem(Personality principal, int number);

	BasketItem updateBasketItem(Personality principal, int number, int quantity);


	Basket closeBasket(Personality principal);
}
