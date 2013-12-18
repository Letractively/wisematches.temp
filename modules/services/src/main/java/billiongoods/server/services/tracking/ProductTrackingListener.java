package billiongoods.server.services.tracking;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductTrackingListener {
	void trackingAdded(ProductTracking tracking);

	void trackingRemoved(ProductTracking tracking);

	void trackingInvalidated(ProductTracking tracking);
}
