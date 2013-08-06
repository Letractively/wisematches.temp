package billiongoods.server.services.basket.impl;

import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.warehouse.Property;

import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateBasketItem implements BasketItem {
	private int quantity;
	private Integer article;

	private Integer[] optionIds;
	private String[] optionValues;

	@Override
	public int getQuantity() {
		throw new UnsupportedOperationException("TODO: Not implemented");
	}

	@Override
	public Integer getArticleId() {
		throw new UnsupportedOperationException("TODO: Not implemented");
	}

	@Override
	public Collection<Property> getProperties() {
		throw new UnsupportedOperationException("TODO: Not implemented");
	}
}
