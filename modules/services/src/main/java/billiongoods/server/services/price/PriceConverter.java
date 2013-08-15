package billiongoods.server.services.price;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class PriceConverter {
	protected PriceConverter() {
	}

	public float convertPrice(float price) {
		return convertPrice(price, getExchangeRate());
	}

	public float convertPrice(float price, float rate) {
		return price * rate;
	}

	public abstract float getExchangeRate();

	public abstract void setExchangeRate(float exchangeRate);

	public static float roundPrice(float price) {
		return Math.round(price * 100f) / 100f;
	}
}
