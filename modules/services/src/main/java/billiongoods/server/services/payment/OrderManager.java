package billiongoods.server.services.payment;

import billiongoods.core.Personality;
import billiongoods.core.search.SearchManager;
import billiongoods.server.services.basket.Basket;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderManager extends SearchManager<Order, OrderContext, Void> {
	void addOrderListener(OrderListener l);

	void removeOrderListener(OrderListener l);


	Order getOrder(Long id);

	Order getByToken(String token);

	Order getByReference(String reference);


	Order create(Personality person, Basket basket, Address address, ShipmentType shipmentType, boolean track);

	void bill(Long orderId, String token);

	void accept(Long orderId, String person, String paymentId, String note);

	void reject(Long orderId, String person, String paymentId, String note);


	void processing(Long orderId, String number, String commentary);

	void shipping(Long orderId, String number, String commentary);

	void shipped(Long orderId, String number, String commentary);


	void failed(Long orderId, String reason);

	void failed(String token, String reason);


	void setOrderTracking(Order order, boolean enable);
}
