package billiongoods.server.web.servlet.mvc.privacy;

import billiongoods.server.services.tracking.ProductTracking;
import billiongoods.server.services.tracking.ProductTrackingManager;
import billiongoods.server.services.tracking.TrackingContext;
import billiongoods.server.warehouse.ProductManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.privacy.form.ProductTrackingView;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/privacy/tracking")
public class TrackingController extends AbstractController {
	private ProductManager productManager;
	private ProductTrackingManager trackingManager;

	public TrackingController() {
	}

	@RequestMapping("")
	public String privacy(Model model) {
		final TrackingContext ctx = new TrackingContext(null, getPrincipal().getId(), null);

		final List<ProductTracking> tracking = trackingManager.searchEntities(ctx, null, null, null);

		final List<ProductTrackingView> views = new ArrayList<>();
		for (ProductTracking t : tracking) {
			views.add(new ProductTrackingView(t.getId(), t.getRegistration(), productManager.getPreview(t.getProductId()), t.getTrackingType()));
		}
		model.addAttribute("tracking", views);

		return "/content/privacy/tracking";
	}


	@RequestMapping("/remove.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse changeTrackingState(@RequestParam("id") Integer id, Locale locale) {
		final ProductTracking tracking = trackingManager.getTracking(id);
		if (!getPrincipal().getId().equals(tracking.getPersonId())) {
			return responseFactory.failure("security");
		}
		trackingManager.removeTracking(id);
		return responseFactory.success();
	}

	@Autowired
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	@Autowired
	public void setTrackingManager(ProductTrackingManager trackingManager) {
		this.trackingManager = trackingManager;
	}
}
