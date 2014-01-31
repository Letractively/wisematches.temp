package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderListener {
	void orderStateChanged(Order order, OrderState oldState, OrderState newState);
}
