package billiongoods.server.services.basket;

import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Basket extends Iterable<BasketItem> {
	float getAmount();

	float getWeight();

	Date getCreationTime();

	Date getUpdatingTime();

	BasketItem getBasketItem(int number);

	List<BasketItem> getBasketItems();
}
