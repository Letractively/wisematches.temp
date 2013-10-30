package billiongoods.server.services.coupon;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class CouponContext {
	private final Integer reference;
	private final CouponReferenceType referenceType;

	public CouponContext(Integer reference, CouponReferenceType referenceType) {
		this.reference = reference;
		this.referenceType = referenceType;
	}

	public Integer getReference() {
		return reference;
	}

	public CouponReferenceType getReferenceType() {
		return referenceType;
	}
}
