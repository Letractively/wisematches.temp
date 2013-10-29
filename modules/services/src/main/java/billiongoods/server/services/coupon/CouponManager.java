package billiongoods.server.services.coupon;

import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.ProductPreview;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface CouponManager {
	/**
	 * Returns coupon by it's id.
	 *
	 * @param id the id of coupon
	 * @return the coupon by specified id or {@code null}  if there is no coupon with specified code.
	 */
	Object getCoupon(Integer id);

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
	 * @param id the id of coupon to be closed
	 * @return closed coupon or {@code null} if there is no coupon with specified id.
	 */
	Coupon closeCoupon(Integer id);


	Coupon createCoupon(String code, double amount, CouponType type, Category category, int count, Date started, Date finished);

	Coupon createCoupon(String code, double amount, CouponType type, ProductPreview product, int count, Date started, Date finished);
}
