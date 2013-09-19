package billiongoods.server.services.price;

import billiongoods.server.warehouse.Price;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PriceConverter {
	Price convert(Price price, double exchangeRate, MarkupType markupType);
}