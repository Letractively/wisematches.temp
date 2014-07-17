package billiongoods.server.web.servlet.mvc.maintain.form;

import billiongoods.server.services.payment.ParcelState;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ParcelStateForm {
	private Long orderId;
	private Long parcelId;
	private String value;
	private String commentary;
	private ParcelState state;

	public ParcelStateForm() {
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getParcelId() {
		return parcelId;
	}

	public void setParcelId(Long parcelId) {
		this.parcelId = parcelId;
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
		sb.append("orderId=").append(orderId);
		sb.append(", parcelId=").append(parcelId);
		sb.append(", value='").append(value).append('\'');
		sb.append(", commentary='").append(commentary).append('\'');
		sb.append(", state=").append(state);
		sb.append('}');
		return sb.toString();
	}
}
