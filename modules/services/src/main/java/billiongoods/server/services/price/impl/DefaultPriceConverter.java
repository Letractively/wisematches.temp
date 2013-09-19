package billiongoods.server.services.price.impl;

import billiongoods.server.services.price.MarkupType;
import billiongoods.server.services.price.PriceConverter;
import billiongoods.server.warehouse.Price;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultPriceConverter implements PriceConverter {
	public DefaultPriceConverter() {
	}

	@Override
	public Price convert(Price p, double exchangeRate, MarkupType markupType) {
		double a = convert(p.getAmount(), exchangeRate, markupType);
		Double pa = p.getPrimordialAmount() == null ? null : convert(p.getPrimordialAmount(), exchangeRate, markupType);
		return new Price(a, pa);
	}

	private double convert(double amount, double exchangeRate, MarkupType markup) {
		return Price.round(amount + amount * markup.getMarkupPercents() / 100.) * exchangeRate + markup.getMarkupFixed();
	}
}
