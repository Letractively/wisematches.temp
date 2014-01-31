package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum Currency {
	RUB(643),
	USD(840);

	private final int code;

	Currency(int code) {
		this.code = code;
	}

	/**
	 * Returns ISO 4217 code for the currency
	 *
	 * @return the ISO 4217 code for the currency.
	 */
	public int getCode() {
		return code;
	}
}
