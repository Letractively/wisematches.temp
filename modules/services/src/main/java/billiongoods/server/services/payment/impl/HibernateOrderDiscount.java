package billiongoods.server.services.payment.impl;

import billiongoods.server.services.payment.OrderDiscount;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class HibernateOrderDiscount implements OrderDiscount {
	@Column(name = "discountAmount", updatable = false)
	private double amount;

	@Column(name = "discountCoupon", updatable = false)
	private String coupon;

	@Deprecated
	HibernateOrderDiscount() {
	}

	public HibernateOrderDiscount(double amount, String coupon) {
		this.amount = amount;
		this.coupon = coupon;
	}

	@Override
	public double getAmount() {
		return amount;
	}

	@Override
	public String getCoupon() {
		return coupon;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateOrderDiscount{");
		sb.append("amount=").append(amount);
		sb.append(", coupon='").append(coupon).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
