package billiongoods.server.services.basket;

import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Basket extends Iterable<BasketItem> {
	Long getId();

	/**
	 * Returns total number of products in the basket
	 *
	 * @return the total number of products in the basket
	 */
	int getProducts();

	/**
	 * Returns total amount for all products in the basket but without processing/shipment cost.
	 *
	 * @return the total amount for all products in the basket but without processing/shipment cost.
	 */
	double getAmount();

	/**
	 * Returns total weight of products in the basket.
	 *
	 * @return the total weight of products in the basket.
	 */
	double getWeight();

	String getCoupon();


	Date getCreationTime();

	Date getUpdatingTime();


	List<BasketItem> getItems();

	BasketItem getItem(int number);
}
