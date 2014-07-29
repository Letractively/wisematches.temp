package billiongoods.server.web.servlet.mvc.maintain.form;

import java.util.Arrays;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderChangeForm {
	private Long orderId;
	private Integer[] items;
	private int[] quantities;

	public OrderChangeForm() {
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer[] getItems() {
		return items;
	}

	public void setItems(Integer[] items) {
		this.items = items;
	}

	public int[] getQuantities() {
		return quantities;
	}

	public void setQuantities(int[] quantities) {
		this.quantities = quantities;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("OrderChangeForm{");
		sb.append("orderId=").append(orderId);
		sb.append(", items=").append(Arrays.toString(items));
		sb.append(", quantities=").append(Arrays.toString(quantities));
		sb.append('}');
		return sb.toString();
	}
}
