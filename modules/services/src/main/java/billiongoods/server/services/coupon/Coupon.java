package billiongoods.server.services.coupon;

import billiongoods.server.warehouse.Catalog;
import billiongoods.server.warehouse.ProductDescription;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Coupon {
	/**
	 * Unique id for this coupon.
	 *
	 * @return the id for this coupon.
	 */
	Integer getId();

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
	Date getCreated();

	/**
	 * Returns time when coupon has been closed. This time hasn't been used in any decisions.
	 *
	 * @return the time when the coupon has been closed.
	 */
	Date getClosure();

	/**
	 * Returns amount of the coupon. Depends on {@link CouponType}
	 * it can be fixed discount, fixed price or perscents.
	 *
	 * @return discount amount
	 * @see #getCouponType()
	 */
	double getAmount();

	/**
	 * Returns type of the coupon.
	 *
	 * @return the type of the coupon.
	 */
	CouponType getCouponType();


	/**
	 * Reference id that can be product or category depends on {@link ReferenceType}.
	 *
	 * @return reference object id.
	 */
	Integer getReferenceId();

	/**
	 * Returns type of object represented by reference id value.
	 *
	 * @return the type of object represented by reference id value.
	 */
	ReferenceType getReferenceType();


	/**
	 * Returns time when coupon can be used after or {@code null} if there is no start time for the coupon
	 *
	 * @return the start time for the coupon or {@code null}
	 */
	Date getStarted();

	/**
	 * Returns time till coupon can be used or {@code null} if there is no finish time for the coupon
	 *
	 * @return the finish time for the coupon or {@code null}
	 */
	Date getFinished();

	/**
	 * Returns number of times when the coupon can be used.
	 *
	 * @return the number of times when the coupon can be used.
	 */
	int getScheduledCount();

	/**
	 * Returns number of elapsed times when the coupon can be used.
	 *
	 * @return the number of elapsed times when the coupon can be used.
	 */
	int getRemainingCount();


	/**
	 * Checks is the coupon still active or not.
	 * <p/>
	 * The coupon is no active if there is no remaining count or it's our of start or finish date.
	 *
	 * @return {@code true} if coupon is active and can be used; {@code false} - otherwise.
	 */
	boolean isActive();


	/**
	 * Calculates new price for the product after applying this coupon.
	 *
	 * @param product the product to be checked.
	 * @return final amount for the product discounted by this coupon.
	 */
	double process(ProductDescription product, Catalog catalog);

	/**
	 * Checks that this coupon can be applied to specified product (or it's category).
	 *
	 * @param product the product to be checked.
	 * @return {@code true} if ; {@code false} -
	 */
	boolean isApplicable(ProductDescription product, Catalog catalog);

}
