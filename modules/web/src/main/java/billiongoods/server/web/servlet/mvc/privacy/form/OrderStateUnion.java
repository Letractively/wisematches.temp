package billiongoods.server.web.servlet.mvc.privacy.form;

import billiongoods.server.services.payment.OrderState;

import java.util.EnumSet;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum OrderStateUnion {
	ALL(null),
	PROCESSING(EnumSet.of(OrderState.ACCEPTED, OrderState.PROCESSING, OrderState.SHIPPING)),
	SUSPENDED(EnumSet.of(OrderState.SUSPENDED)),
	DELIVERING(EnumSet.of(OrderState.SHIPPED)),
	BILLING(EnumSet.of(OrderState.NEW, OrderState.BILLING)),
	BROKEN(EnumSet.of(OrderState.CANCELLED, OrderState.FAILED));

	private final String code;
	private final EnumSet<OrderState> orderStates;

	OrderStateUnion(EnumSet<OrderState> orderStates) {
		this.code = name().toLowerCase();
		this.orderStates = orderStates;
	}

	public String getCode() {
		return code;
	}

	public EnumSet<OrderState> getOrderStates() {
		return orderStates;
	}

	public static OrderStateUnion byCode(String orderState) {
		if (orderState == null) {
			return null;
		}

		for (OrderStateUnion mapping : values()) {
			if (mapping.getCode().equalsIgnoreCase(orderState)) {
				return mapping;
			}
		}
		return null;
	}
}
