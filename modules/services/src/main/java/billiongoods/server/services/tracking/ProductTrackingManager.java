package billiongoods.server.services.tracking;

import billiongoods.core.Personality;
import billiongoods.core.search.SearchManager;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductTrackingManager extends SearchManager<ProductTracking, TrackingContext, Void> {
	void addProductTrackingListener(ProductTrackingListener l);

	void removeProductTrackingListener(ProductTrackingListener l);


	ProductTracking createTracking(Integer productId, String email, TrackingType trackingType);

	ProductTracking createTracking(Integer productId, Personality person, TrackingType trackingType);


	ProductTracking getTracking(Integer id);

	ProductTracking removeTracking(Integer id);
}
