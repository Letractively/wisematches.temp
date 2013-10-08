package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.core.search.Order;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum SortingType {
    BESTSELLING("bs", "soldCount", false),
    PRICE_DOWN("plth", "price", true),
    PRICE_UP("phtl", "price", false),
    ARRIVAL_DATE("d", "registrationDate", false);

    private final String code;
    private final Order order;

    private static final SortingType[] values = SortingType.values();

    SortingType(String code, String property, boolean ask) {
        this.code = code;
        this.order = ask ? Order.asc(property) : Order.desc(property);
    }

    public String getCode() {
        return code;
    }

    public Order getOrder() {
        return order;
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
