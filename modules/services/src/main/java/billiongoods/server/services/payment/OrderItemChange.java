package billiongoods.server.services.payment;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderItemChange {
	private final Integer item;
	private final int quantity;

	public OrderItemChange(Integer item, int quantity) {
		this.item = item;
		this.quantity = quantity;
	}

	public Integer getItem() {
		return item;
	}

	public int getQuantity() {
		return quantity;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("OrderItemChange{");
		sb.append("item=").append(item);
		sb.append(", quantity=").append(quantity);
		sb.append('}');
		return sb.toString();
	}
}
