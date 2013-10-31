package billiongoods.server.services.basket;

import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Basket extends Iterable<BasketItem> {
	Long getId();

	double getAmount();

	double getWeight();


	String getCoupon();


	Date getCreationTime();

	Date getUpdatingTime();


	List<BasketItem> getBasketItems();

	BasketItem getBasketItem(int number);
}
