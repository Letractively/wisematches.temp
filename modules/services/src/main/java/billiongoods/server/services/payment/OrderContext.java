package billiongoods.server.services.payment;

import billiongoods.core.Personality;

import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderContext {
	private final Set<OrderState> orderStates;
	private final Personality personality;

	public OrderContext(Set<OrderState> orderStates) {
		this(null, orderStates);
	}

	public OrderContext(Personality personality, Set<OrderState> orderStates) {
		this.orderStates = orderStates;
		this.personality = personality;
	}

	public Personality getPersonality() {
		return personality;
	}

	public Set<OrderState> getOrderStates() {
		return orderStates;
	}
}
