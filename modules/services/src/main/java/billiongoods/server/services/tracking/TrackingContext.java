package billiongoods.server.services.tracking;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class TrackingContext {
	private Integer productId;
	private TrackingType trackingType;
	private TrackingPerson trackingPerson;

	public TrackingContext(Integer productId, TrackingType trackingType) {
		this.productId = productId;
		this.trackingType = trackingType;
	}

	public TrackingContext(Integer productId, TrackingType trackingType, TrackingPerson trackingPerson) {
		this.productId = productId;
		this.trackingType = trackingType;
		this.trackingPerson = trackingPerson;
	}

	public Integer getProductId() {
		return productId;
	}

	public TrackingType getTrackingType() {
		return trackingType;
	}

	public TrackingPerson getTrackingPerson() {
		return trackingPerson;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("TrackingContext{");
		sb.append("productId=").append(productId);
		sb.append(", trackingType=").append(trackingType);
		sb.append(", trackingPerson=").append(trackingPerson);
		sb.append('}');
		return sb.toString();
	}
}