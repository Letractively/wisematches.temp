package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.core.search.Order;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum ItemSortType {
	BESTSELLING("bs", "soldCount", false),
	PRICE_DOWN("plth", "price", true),
	PRICE_UP("phtl", "price", false),
	NEW_ARRIVAL("d", "registrationDate", false);

	private final String name;
	private final Order order;

	ItemSortType(String name, String property, boolean ask) {
		this.name = name;
		this.order = ask ? Order.asc(property) : Order.desc(property);
	}

	public String getName() {
		return name;
	}

	public Order getOrder() {
		return order;
	}
}
