package billiongoods.server.web.servlet.mvc.warehouse.form;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OrderTrackingForm {
	private Long order;
	private String email;
	private boolean enable;

	public OrderTrackingForm() {
	}

	public Long getOrder() {
		return order;
	}

	public void setOrder(Long order) {
		this.order = order;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Override
	public String toString() {
		return "OrderTrackingForm{" +
				"order=" + order +
				", email='" + email + '\'' +
				", enable=" + enable +
				'}';
	}
}
