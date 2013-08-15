package billiongoods.server.services.payment;

import billiongoods.core.Personality;
import billiongoods.core.search.SearchManager;
import billiongoods.server.services.basket.Basket;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderManager extends SearchManager<Order, OrderContext> {
	void addOrderListener(OrderListener l);

	void removeOrderListener(OrderListener l);


	Order getOrder(Long id);

	Order getByToken(String token);

	Order getByReference(String reference);


	Order create(Personality person, Basket basket, Address address, ShipmentType shipmentType, boolean track);

	void bill(Long orderId, String token);

	void accept(Long orderId, String person, String paymentId);

	void reject(Long orderId, String person, String paymentId);

	void processing(Long orderId, String number);

	void shipping(Long orderId, String number);

	void shipped(Long orderId, String number);


	void failed(Long orderId, String reason);

	void failed(String token, String reason);


	void setOrderTracking(Order order, boolean enable);
}
