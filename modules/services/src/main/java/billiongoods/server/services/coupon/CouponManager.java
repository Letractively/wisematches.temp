package billiongoods.server.services.coupon;

import billiongoods.core.search.SearchManager;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.ProductPreview;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface CouponManager extends SearchManager<Coupon, CouponContext, Void> {
	/**
	 * Returns coupon by it's code.
	 *
	 * @param code the code of coupon
	 * @return the coupon by specified code or {@code null} if there is no coupon with specified code.
	 */
	Coupon getCoupon(String code);

	/**
	 * Close coupon with specified id.
	 *
	 * @param code the id of coupon to be closed
	 * @return closed coupon or {@code null} if there is no coupon with specified id.
	 */
	Coupon closeCoupon(String code);

	/**
	 * Indicates that coupon with specified code was used
	 *
	 * @param code the id of coupon to be closed
	 * @return the coupon or {@code null} if it wasn't redeemed
	 */
	Coupon redeemCoupon(String code);


	Coupon createCoupon(String code, double amount, CouponAmountType amountType, Category category, int count, Date termination);

	Coupon createCoupon(String code, double amount, CouponAmountType amountType, ProductPreview product, int count, Date termination);
}
