package billiongoods.server.web.servlet.mvc.maintain.form;

import billiongoods.server.services.payment.OrderState;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderStateForm {
	private Long id;
	private OrderState state;
	private String value;
	private String commentary;

	public OrderStateForm() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OrderState getState() {
		return state;
	}

	public void setState(OrderState state) {
		this.state = state;
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
}
