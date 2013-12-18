package billiongoods.server.services.tracking;

import billiongoods.core.account.Account;
import billiongoods.core.search.SearchManager;

import java.util.EnumSet;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ProductTrackingManager extends SearchManager<ProductTracking, TrackingContext, Void> {
	void addProductTrackingListener(ProductTrackingListener l);

	void removeProductTrackingListener(ProductTrackingListener l);


	int importAccountTracking(Account account);


	ProductTracking createTracking(Integer productId, TrackingPerson tracker, TrackingType type);

	ProductTracking removeTracking(Integer productId, TrackingPerson tracker, TrackingType type);


	EnumSet<TrackingType> containsTracking(Integer productId, TrackingPerson tracker);

	Object getTracking(Integer productId, TrackingPerson tracker, TrackingType type);
}
