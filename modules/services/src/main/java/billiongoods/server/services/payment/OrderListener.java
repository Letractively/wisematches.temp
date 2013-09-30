package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderListener {
	void orderStateChange(Order order, OrderState oldState, OrderState newState);
}
