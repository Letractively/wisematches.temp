package billiongoods.server.web.servlet.mvc.maintain.form;

import billiongoods.server.services.tracking.ProductTracking;
import billiongoods.server.warehouse.ProductPreview;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class TrackingSummary {
	private final Map<ProductPreview, List<ProductTracking>> trackingSummary;

	public TrackingSummary(Map<ProductPreview, List<ProductTracking>> trackingSummary) {
		this.trackingSummary = trackingSummary;
	}

	public Collection<ProductPreview> getProducts() {
		return trackingSummary.keySet();
	}

	public List<ProductTracking> getProductTrackings(ProductPreview preview) {
		return trackingSummary.get(preview);
	}
}
