package billiongoods.server.services.coupon;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum CouponType {
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
	PERCENT
}
