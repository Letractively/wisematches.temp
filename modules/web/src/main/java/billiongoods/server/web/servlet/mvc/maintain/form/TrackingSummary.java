package billiongoods.server.web.servlet.mvc.maintain.form;

import billiongoods.server.services.tracking.ProductTracking;
import billiongoods.server.warehouse.ProductDescription;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class TrackingSummary {
	private final Map<ProductDescription, List<ProductTracking>> trackingSummary;

	public TrackingSummary(Map<ProductDescription, List<ProductTracking>> trackingSummary) {
		this.trackingSummary = trackingSummary;
	}

	public Collection<ProductDescription> getProducts() {
		return trackingSummary.keySet();
	}

	public List<ProductTracking> getProductTrackings(ProductDescription description) {
		return trackingSummary.get(description);
	}
}
