package billiongoods.server.services.tracking;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class TrackingContext {
	private String email;
	private Long personId;
	private Integer productId;
	private TrackingType trackingType;

	public TrackingContext(Integer productId, TrackingType trackingType) {
		this.productId = productId;
		this.trackingType = trackingType;
	}

	public TrackingContext(Integer productId, String email, TrackingType trackingType) {
		this.email = email;
		this.productId = productId;
		this.trackingType = trackingType;
	}

	public TrackingContext(Integer productId, Long personId, TrackingType trackingType) {
		this.personId = personId;
		this.productId = productId;
		this.trackingType = trackingType;
	}

	public String getEmail() {
		return email;
	}

	public Long getPersonId() {
		return personId;
	}

	public Integer getProductId() {
		return productId;
	}

	public TrackingType getTrackingType() {
		return trackingType;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("TrackingContext{");
		sb.append("productId=").append(productId);
		sb.append(", personId=").append(personId);
		sb.append(", email='").append(email).append('\'');
		sb.append('}');
		return sb.toString();
	}
}