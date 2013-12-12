package billiongoods.server.services.tracking;

import billiongoods.core.Personality;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class TrackingContext {
	private String email;
	private Integer productId;
	private Personality personality;
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

	public TrackingContext(Integer productId, Personality personality, TrackingType trackingType) {
		this.productId = productId;
		this.personality = personality;
		this.trackingType = trackingType;
	}

	public String getEmail() {
		return email;
	}

	public Personality getPersonality() {
		return personality;
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
		sb.append(", personality=").append(personality);
		sb.append(", email='").append(email).append('\'');
		sb.append('}');
		return sb.toString();
	}
}