package billiongoods.server.services.price;

import billiongoods.server.warehouse.Price;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MarkupCalculator {
	private double markupFixed = 0.30;
	private double markupPercents = 0.20;

	public MarkupCalculator() {
	}

	public MarkupCalculator(double markupFixed, double markupPercents) {
		this.markupFixed = markupFixed;
		this.markupPercents = markupPercents;
	}

	public double getMarkupFixed() {
		return markupFixed;
	}

	public double getMarkupPercents() {
		return markupPercents;
	}

	public Price calculateMarkupPrice(Price p) {
		double a = Price.round(p.getAmount() + p.getAmount() * markupPercents + markupFixed);
		Double pa = p.getPrimordialAmount() == null ? null : Price.round(p.getPrimordialAmount() + p.getPrimordialAmount() * markupPercents + markupFixed);
		return new Price(a, pa);
	}

	@Deprecated
	public double calculateFinalPrice(double price) {
		return Price.round(price + price * markupPercents + markupFixed);
	}

	@Deprecated
	public Double calculateFinalPrice(Double price) {
		if (price == null) {
			return null;
		}
		return calculateFinalPrice(price.doubleValue());
	}

	public String getJavaScriptFunction(String price) {
		return "(function(price) {return (price + price * " + markupPercents + " + " + markupFixed + ").toFixed(2)})(" + price + ")";
	}
}
