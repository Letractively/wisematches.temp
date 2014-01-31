package billiongoods.server.services.coupon;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class CouponContext {
	private final Long personId;
	private final Integer reference;
	private final CouponReferenceType referenceType;

	public CouponContext(Integer reference, CouponReferenceType referenceType) {
		this(null, reference, referenceType);
	}

	public CouponContext(Long personId, Integer reference, CouponReferenceType referenceType) {
		this.personId = personId;
		this.reference = reference;
		this.referenceType = referenceType;
	}

	public Long getPersonId() {
		return personId;
	}

	public Integer getReference() {
		return reference;
	}

	public CouponReferenceType getReferenceType() {
		return referenceType;
	}
}
