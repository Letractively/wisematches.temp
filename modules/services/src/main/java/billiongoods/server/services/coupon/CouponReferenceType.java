package billiongoods.server.services.coupon;

/**
 * Indicates is a coupon reffers to product or to category. Depends on this {@code referenceId} should
 * be interpretated in differ ways.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum CouponReferenceType {
	PRODUCT,
	CATEGORY,
	EVERYTHING;

	public boolean isProduct() {
		return this == PRODUCT;
	}

	public boolean isCategory() {
		return this == CATEGORY;
	}

	public boolean isEverything() {
		return this == EVERYTHING;
	}
}
