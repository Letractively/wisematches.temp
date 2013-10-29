package billiongoods.server.web.servlet.mvc.maintain.form;

import billiongoods.server.services.coupon.CouponType;
import billiongoods.server.services.coupon.ReferenceType;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class CouponForm {
	private String code;

	private double amount;

	private CouponType couponType;

	private int referenceId;

	private ReferenceType referenceType;


	private int count;

	private String started;

	private String finished;


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

	public CouponType getCouponType() {
		return couponType;
	}

	public void setCouponType(CouponType couponType) {
		this.couponType = couponType;
	}

	public int getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(int referenceId) {
		this.referenceId = referenceId;
	}

	public ReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(ReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getStarted() {
		return started;
	}

	public void setStarted(String started) {
		this.started = started;
	}

	public String getFinished() {
		return finished;
	}

	public void setFinished(String finished) {
		this.finished = finished;
	}
}
