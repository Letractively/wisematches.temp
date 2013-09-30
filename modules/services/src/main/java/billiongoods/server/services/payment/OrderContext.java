package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderContext {
	private final OrderState orderState;

	public OrderContext(OrderState orderState) {
		this.orderState = orderState;
	}

	public OrderState getOrderState() {
		return orderState;
	}
}
