package billiongoods.server.web.servlet.mvc.maintain.form;

import billiongoods.server.services.coupon.CouponAmountType;
import billiongoods.server.services.coupon.CouponReferenceType;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class CouponForm {
	private String code;


	private double amount;

	private CouponAmountType amountType;


	private Integer reference;

	private CouponReferenceType referenceType;


	private int allocatedCount;

	private String termination;


	public CouponForm() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public CouponAmountType getAmountType() {
		return amountType;
	}

	public void setAmountType(CouponAmountType amountType) {
		this.amountType = amountType;
	}

	public Integer getReference() {
		return reference;
	}

	public void setReference(Integer reference) {
		this.reference = reference;
	}

	public CouponReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(CouponReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public int getAllocatedCount() {
		return allocatedCount;
	}

	public void setAllocatedCount(int allocatedCount) {
		this.allocatedCount = allocatedCount;
	}

	public String getTermination() {
		return termination;
	}

	public void setTermination(String termination) {
		this.termination = termination;
	}
}
