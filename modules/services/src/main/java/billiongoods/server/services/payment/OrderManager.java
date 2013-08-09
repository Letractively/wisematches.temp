package billiongoods.server.services.payment;

import billiongoods.core.Personality;
import billiongoods.server.services.basket.Basket;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderManager {
	Order createOrder(Personality person, Basket basket, Address address, PaymentSystem system);

	void deleteOrder(Long orderId);


	void changeState(Long orderId, OrderState state);
}
