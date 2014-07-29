package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderListener {
	void orderRefund(Order order, String token, double amount);

	void orderStateChanged(Order order, OrderState oldState, OrderState newState);

	void orderContentChanged(Order order, OrderItemChange... changes);
}
