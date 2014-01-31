package billiongoods.server.web.servlet.mvc.privacy.form;

import billiongoods.server.services.tracking.TrackingType;
import billiongoods.server.warehouse.ProductPreview;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ProductTrackingView {
	private final Date registered;
	private final ProductPreview product;
	private final TrackingType trackingType;

	public ProductTrackingView(Date registered, ProductPreview product, TrackingType trackingType) {
		this.registered = registered;
		this.product = product;
		this.trackingType = trackingType;
	}

	public Date getRegistered() {
		return registered;
	}

	public ProductPreview getProduct() {
		return product;
	}

	public TrackingType getTrackingType() {
		return trackingType;
	}
}
