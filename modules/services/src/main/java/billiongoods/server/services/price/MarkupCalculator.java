package billiongoods.server.services.price;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MarkupCalculator {
	private double markupFixed = 0.30;
	private double markupPercents = 0.20;

	public MarkupCalculator() {
	}

	public double getMarkupFixed() {
		return markupFixed;
	}

	public double getMarkupPercents() {
		return markupPercents;
	}

	public double calculateFinalPrice(double price) {
		return PriceMath.round(price + price * markupPercents + markupFixed);
	}

	public String getJavaScriptFunction(String price) {
		return "(function(price) {return (price + price * " + markupPercents + " + " + markupFixed + ").toFixed(2)})(" + price + ")";
	}
}
