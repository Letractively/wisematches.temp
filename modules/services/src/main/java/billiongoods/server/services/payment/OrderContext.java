package billiongoods.server.services.payment;

import billiongoods.core.Personality;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderContext {
	private final OrderState orderState;
	private final Personality personality;

	public OrderContext(OrderState orderState) {
		this(null, orderState);
	}

	public OrderContext(Personality personality, OrderState orderState) {
		this.orderState = orderState;
		this.personality = personality;
	}

	public Personality getPersonality() {
		return personality;
	}

	public OrderState getOrderState() {
		return orderState;
	}
}
