package billiongoods.server.web.servlet.mvc.warehouse.form;

import java.util.Arrays;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BasketItemForm {
	private int quantity;
	private Integer product;

	private Integer[] optionIds;
	private String[] optionValues;

	public BasketItemForm() {
	}

	public Integer getProduct() {
		return product;
	}

	public void setProduct(Integer product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public Integer[] getOptionIds() {
		return optionIds;
	}

	public void setOptionIds(Integer[] optionIds) {
		this.optionIds = optionIds;
	}

	public String[] getOptionValues() {
		return optionValues;
	}

	public void setOptionValues(String[] optionValues) {
		this.optionValues = optionValues;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("BasketItemForm{");
		sb.append("product=").append(product);
		sb.append(", quantity=").append(quantity);
		sb.append(", optionIds=").append(Arrays.toString(optionIds));
		sb.append(", optionValues=").append(Arrays.toString(optionValues));
		sb.append('}');
		return sb.toString();
	}
}
