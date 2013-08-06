package billiongoods.server.services.basket.impl;

import billiongoods.core.Personality;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.basket.BasketManager;
import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.Property;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MemoryBasketManager implements BasketManager {
	private final Map<Personality, AbstractHibernateBasket> basketMap = new HashMap<>();

	public MemoryBasketManager() {
	}

	@Override
	public Basket getBasket(Personality principal) {
		return basketMap.get(principal);
	}

	@Override
	public Basket closeBasket(Personality principal) {
		return basketMap.remove(principal);
	}

	@Override
	public BasketItem addBasketItem(Personality principal, ArticleDescription article, List<Property> options, int quantity) {
		AbstractHibernateBasket basket = basketMap.get(principal);
		if (basket == null) {
			basket = new AbstractHibernateBasket();
			basketMap.put(principal, basket);
		}

		final HibernateBasketItem item = new HibernateBasketItem(article, options, quantity);
		basket.addBasketItem(item);
		return item;
	}

	@Override
	public BasketItem removeBasketItem(Personality principal, Integer basketItemId) {
		return null;
	}

	@Override
	public BasketItem updateBasketItem(Personality principal, Integer basketItemId, int quantity) {
		return null;
	}
}
