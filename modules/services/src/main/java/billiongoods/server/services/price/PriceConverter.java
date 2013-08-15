package billiongoods.server.services.price;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PriceConverter {
	private float exchangeRate = 33.33f;

	public PriceConverter() {
	}

	public float convertPrice(float price) {
		return convertPrice(price, exchangeRate);
	}

	public float convertPrice(float price, float rate) {
		return price * rate;
	}

	public float getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(float exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public static float roundPrice(float price) {
		return Math.round(price * 100f) / 100f;
	}
}
