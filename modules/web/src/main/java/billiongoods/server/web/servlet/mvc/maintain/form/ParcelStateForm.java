package billiongoods.server.web.servlet.mvc.maintain.form;

import billiongoods.server.services.payment.ParcelState;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ParcelStateForm {
	private Long order;
	private int number;
	private String value;
	private String commentary;
	private ParcelState state;

	public ParcelStateForm() {
	}

	public Long getOrder() {
		return order;
	}

	public void setOrder(Long order) {
		this.order = order;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCommentary() {
		return commentary;
	}

	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	public ParcelState getState() {
		return state;
	}

	public void setState(ParcelState state) {
		this.state = state;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ParcelStateForm{");
		sb.append("commentary='").append(commentary).append('\'');
		sb.append(", value='").append(value).append('\'');
		sb.append(", number=").append(number);
		sb.append(", order=").append(order);
		sb.append('}');
		return sb.toString();
	}
}
