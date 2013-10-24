package billiongoods.server.services.payment;

import java.util.Map;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrdersSummary {
	private final Map<OrderState, Integer> ordersCount;

	public OrdersSummary(Map<OrderState, Integer> ordersCount) {
		this.ordersCount = ordersCount;
	}

	public Set<OrderState> getOrderStates() {
		return ordersCount.keySet();
	}

	public int getOrdersCount(OrderState state) {
		final Integer integer = ordersCount.get(state);
		if (integer == null) {
			return 0;
		}
		return integer;
	}
}
