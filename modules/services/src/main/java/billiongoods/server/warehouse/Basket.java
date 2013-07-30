package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Basket {
    long getId();

    List<BasketItem> getBasketItems();
}
