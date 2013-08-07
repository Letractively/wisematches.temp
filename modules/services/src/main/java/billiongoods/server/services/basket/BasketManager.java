package billiongoods.server.services.basket;

import billiongoods.core.Personality;
import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.Property;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface BasketManager {
	Basket getBasket(Personality principal);

	Basket closeBasket(Personality principal);


	BasketItem addBasketItem(Personality principal, ArticleDescription article, List<Property> options, int quantity);

	BasketItem removeBasketItem(Personality principal, int number);

	BasketItem updateBasketItem(Personality principal, int number, int quantity);
}
