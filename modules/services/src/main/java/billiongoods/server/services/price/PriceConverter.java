package billiongoods.server.services.price;

import billiongoods.server.warehouse.Price;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface PriceConverter {
	void addExchangeRateListener(ExchangeRateListener l);

	void removeExchangeRateListener(ExchangeRateListener l);


	double getExchangeRate();

	void setExchangeRate(double exchangeRate);


	Price convert(Price p, MarkupType markup);

	String formula(String name, String roundFunction, MarkupType markup);
}