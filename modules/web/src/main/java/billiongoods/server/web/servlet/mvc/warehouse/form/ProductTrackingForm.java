package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.server.services.tracking.TrackingType;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ProductTrackingForm {
	private Integer productId;
	private String email;
	private TrackingType type;
	private TrackingChangeType changeType;

	public ProductTrackingForm() {
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public TrackingType getType() {
		return type;
	}

	public void setType(TrackingType type) {
		this.type = type;
	}

	public TrackingChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(TrackingChangeType changeType) {
		this.changeType = changeType;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ProductTrackingForm{");
		sb.append("productId=").append(productId);
		sb.append(", email='").append(email).append('\'');
		sb.append(", type=").append(type);
		sb.append(", changeType=").append(changeType);
		sb.append('}');
		return sb.toString();
	}
}
