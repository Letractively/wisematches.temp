package billiongoods.server.services.basket;

import billiongoods.core.Personality;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface BasketManager {
	Basket getBasket(Personality principal);
}
