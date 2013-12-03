package billiongoods.server.services.coupon;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum CouponAmountType {
	/**
	 * Indicates that a coupon contains fixed price
	 */
	PRICE,

	/**
	 * Indicates that a coupon contains fixed discount amount.
	 */
	FIXED,

	/**
	 * Indicates that a coupon contains discount percents.
	 */
	PERCENT;

	public boolean isPrice() {
		return this == PRICE;
	}

	public boolean isFixed() {
		return this == FIXED;
	}

	public boolean isPercent() {
		return this == PERCENT;
	}
}
