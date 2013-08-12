package billiongoods.core.search;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class Order {
	private boolean ascending;
	private String propertyName;

	/**
	 * Constructor for Order.
	 *
	 * @param propertyName name of the column
	 * @param ascending    the sorting direction
	 */
	protected Order(String propertyName, boolean ascending) {
		this.propertyName = propertyName;
		this.ascending = ascending;
	}

	/**
	 * Ascending order
	 *
	 * @param propertyName
	 * @return Order
	 */
	public static Order asc(String propertyName) {
		return new Order(propertyName, true);
	}

	/**
	 * Descending order
	 *
	 * @param propertyName
	 * @return Order
	 */
	public static Order desc(String propertyName) {
		return new Order(propertyName, false);
	}

	public boolean isAscending() {
		return ascending;
	}

	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public String toString() {
		return propertyName + " " + (ascending ? "asc" : "desc");
	}
}