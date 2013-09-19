package billiongoods.server.services.price;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum MarkupType {
	NONE(0., 0.),
	REGULAR(10., 20.);

	private final double markupFixed;
	private final double markupPercents;

	MarkupType(double markupFixed, double markupPercents) {
		this.markupFixed = markupFixed;
		this.markupPercents = markupPercents;
	}

	public double getMarkupFixed() {
		return markupFixed;
	}

	public double getMarkupPercents() {
		return markupPercents;
	}
}
