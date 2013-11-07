package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.core.search.Order;
import billiongoods.core.search.Orders;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum SortingType {
	RELEVANCE("r", null, false),
	BESTSELLING("bs", "soldCount", false),
	PRICE_DOWN("plth", "price", true),
	PRICE_UP("phtl", "price", false),
	ARRIVAL_DATE("d", "registrationDate", false);

	private final String code;
	private final Orders orders;

	private static final SortingType[] values = SortingType.values();

	SortingType(String code, String property, boolean ask) {
		this.code = code;
		this.orders = property == null ? null : Orders.of(ask ? Order.asc(property) : Order.desc(property));
	}

	public String getCode() {
		return code;
	}

	public Orders getOrders() {
		return orders;
	}

	public static SortingType byCode(String code) {
		for (SortingType sortingType : values) {
			if (sortingType.code.equalsIgnoreCase(code)) {
				return sortingType;
			}
		}
		return null;
	}

}
