package billiongoods.server.services.payment;

import billiongoods.core.Personality;

import java.util.EnumSet;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderContext {
	private final EnumSet<OrderState> orderStates;
	private final Personality personality;

	public OrderContext(EnumSet<OrderState> orderStates) {
		this(null, orderStates);
	}

	public OrderContext(Personality personality, EnumSet<OrderState> orderStates) {
		this.orderStates = orderStates;
		this.personality = personality;
	}

	public Personality getPersonality() {
		return personality;
	}

	public EnumSet<OrderState> getOrderStates() {
		return orderStates;
	}
}
