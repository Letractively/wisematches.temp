package billiongoods.server.services.payment;

import java.util.EnumSet;
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

	public int getTotalCount() {
		int res = 0;
		for (Integer integer : ordersCount.values()) {
			if (integer != null) {
				res += integer;
			}
		}
		return res;
	}

	public int getOrdersCount(OrderState state) {
		final Integer integer = ordersCount.get(state);
		if (integer == null) {
			return 0;
		}
		return integer;
	}

	public int getOrdersCount(EnumSet<OrderState> states) {
		if (states == null) {
			return getTotalCount();
		}

		int res = 0;
		for (OrderState state : states) {
			final Integer integer = ordersCount.get(state);
			if (integer != null) {
				res += integer;
			}
		}
		return res;
	}
}
