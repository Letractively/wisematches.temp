package billiongoods.server.services.coupon;

import billiongoods.server.services.basket.Basket;
import billiongoods.server.warehouse.Catalog;
import billiongoods.server.warehouse.ProductPreview;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Coupon {
	/**
	 * Returns unique code for this coupon.
	 *
	 * @return the unique code for this coupon.
	 */
	String getCode();


	/**
	 * Return time when coupon has been created. This time hasn't been used in any decisions.
	 *
	 * @return the time when the coupon has been created.
	 */
	Date getCreation();

	/**
	 * Returns time when coupon has been closed. This time hasn't been used in any decisions.
	 *
	 * @return the time when the coupon has been closed.
	 */
	Date getTermination();


	/**
	 * Returns amount of the coupon. Depends on {@link CouponAmountType}
	 * it can be fixed discount, fixed price or perscents.
	 *
	 * @return discount amount
	 * @see #getAmountType()
	 */
	double getAmount();

	/**
	 * Returns type of the coupon.
	 *
	 * @return the type of the coupon.
	 */
	CouponAmountType getAmountType();


	/**
	 * Reference id that can be product or category depends on {@link CouponReferenceType}.
	 *
	 * @return reference object id.
	 */
	Integer getReference();

	/**
	 * Returns type of object represented by reference id value.
	 *
	 * @return the type of object represented by reference id value.
	 */
	CouponReferenceType getReferenceType();

	/**
	 * Returns number of utilized coupons.
	 *
	 * @return the number of utilized coupons. Never negative number. Zero if coupon wasn't used.
	 */
	int getUtilizedCount();

	/**
	 * Returns number of allocated coupons.
	 *
	 * @return the number of allocated coupons. Zero if unlimited number of coupons was allocated.
	 */
	int getAllocatedCount();

	/**
	 * Returns date of last utilization.
	 *
	 * @return the date of last utilization or {@code null} if coupon hasn't been used yet.
	 */
	Date getLastUtilization();

	/**
	 * Checks is the coupon still active or not.
	 * <p/>
	 * The coupon is no active if there is no remaining count or it's our of start or finish date.
	 *
	 * @return {@code true} if coupon is active and can be used; {@code false} - otherwise.
	 */
	boolean isActive();

	/**
	 * Checks is this coupon was terminated.
	 *
	 * @return {@code true} if the coupon is terminated; {@code false} - otherwise.
	 */
	boolean isTerminated();

	/**
	 * Checks is this coupon was fully utilized.
	 *
	 * @return {@code true} if the coupon was fully utilized; {@code false} - otherwise.
	 */
	boolean isFullyUtilized();


	/**
	 * Calculates discount for whole basket.
	 *
	 * @param basket the basket to be recalculated.
	 * @return discount for specified basket by this coupon.
	 */
	double getDiscount(Basket basket, Catalog catalog);

	/**
	 * Calculates discount for specified product.
	 *
	 * @param product the product to be checked.
	 * @return discount for specified product by this coupon.
	 */
	double getDiscount(ProductPreview product, Catalog catalog);


	/**
	 * Checks that this coupon can be applied to specified product (or it's category).
	 *
	 * @param product the product to be checked.
	 * @return {@code true} if ; {@code false} -
	 */
	boolean isApplicable(ProductPreview product, Catalog catalog);
}
