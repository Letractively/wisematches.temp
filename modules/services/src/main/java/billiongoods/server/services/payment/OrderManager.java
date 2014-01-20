package billiongoods.server.services.payment;

import billiongoods.core.Personality;
import billiongoods.core.account.Account;
import billiongoods.core.search.SearchManager;
import billiongoods.server.services.address.Address;
import billiongoods.server.services.basket.Basket;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderManager extends SearchManager<Order, OrderContext, Void> {
	void addOrderListener(OrderListener l);

	void removeOrderListener(OrderListener l);


	Order getOrder(Long id);

	Order getByToken(String token);

	Order getByReference(String reference);


	OrdersSummary getOrdersSummary();

	OrdersSummary getOrdersSummary(Personality principal);


	int importAccountOrders(Account account);


	Order create(Personality person, Basket basket, Address address, ShipmentType shipmentType, boolean track);

	void bill(Long orderId, String token);


	void failed(Long orderId, String reason);

	void failed(String token, String reason);


	void accept(Long orderId, String payer, String payerName, String payerNote, String paymentId);

	void reject(Long orderId, String person, String paymentId, String note);


	void processing(Long orderId, String number, String commentary);

	void shipping(Long orderId, String number, String commentary);

	void shipped(Long orderId, String number, String commentary);


	void cancel(Long orderId, String refundId, String commentary);

	void suspend(Long orderId, Date resumeDate, String commentary);


	void close(Long orderId, Date deliveryDate, String commentary);

	void remove(Long orderId);


	void setOrderTracking(Order order, boolean enable);
}
