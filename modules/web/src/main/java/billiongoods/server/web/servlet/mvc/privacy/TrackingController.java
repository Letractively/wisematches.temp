package billiongoods.server.web.servlet.mvc.privacy;

import billiongoods.core.Member;
import billiongoods.server.services.tracking.*;
import billiongoods.server.warehouse.ProductManager;
import billiongoods.server.warehouse.ProductPreview;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.privacy.form.ProductTrackingView;
import billiongoods.server.web.servlet.mvc.warehouse.form.ProductTrackingForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.EnumSet;
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
		final TrackingContext ctx = new TrackingContext(null, null, TrackingPerson.of(getMember()));

		final List<ProductTracking> tracking = trackingManager.searchEntities(ctx, null, null, null);

		final List<ProductTrackingView> views = new ArrayList<>();
		for (ProductTracking t : tracking) {
			views.add(new ProductTrackingView(t.getRegistration(), productManager.getPreview(t.getProductId()), t.getTrackingType()));
		}
		model.addAttribute("tracking", views);
		return "/content/privacy/tracking";
	}

	@RequestMapping("/add.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse addProductTracking(@RequestBody ProductTrackingForm form, Locale locale) {
		return processTrackingState(form, true, locale);
	}

	@RequestMapping("/remove.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse removeProductTracking(@RequestBody ProductTrackingForm form, Locale locale) {
		return processTrackingState(form, false, locale);
	}

	private ServiceResponse processTrackingState(ProductTrackingForm form, boolean add, Locale locale) {
		if (form.getProductId() == null) {
			return responseFactory.failure("product.subscribe.error.unknown", locale);
		}

		final ProductPreview preview = productManager.getPreview(form.getProductId());
		if (preview == null) {
			return responseFactory.failure("product.subscribe.error.unknown", locale);
		}

		TrackingPerson trackingPerson;
		final Member member = getMember();
		if (member != null) {
			trackingPerson = TrackingPerson.of(member);
		} else {
			if (form.getEmail() == null || form.getEmail().isEmpty()) {
				return responseFactory.failure("product.subscribe.error.email", locale);
			}
			trackingPerson = TrackingPerson.of(form.getEmail());
		}

		final EnumSet<TrackingType> trackingTypes = trackingManager.containsTracking(form.getProductId(), trackingPerson);
		if (add) {
			if (trackingTypes.contains(form.getType())) {
				return responseFactory.failure("product.subscribe.error.subscribed", locale);
			}

			ProductTracking tracking;
			if (member != null) {
				tracking = trackingManager.createTracking(form.getProductId(), trackingPerson, form.getType());
			} else {
				tracking = trackingManager.createTracking(form.getProductId(), trackingPerson, form.getType());
			}
			return responseFactory.success(tracking);
		} else {
			if (!trackingTypes.contains(form.getType())) {
				return responseFactory.failure("product.subscribe.error.unknown", locale);
			}
			ProductTracking tracking = trackingManager.removeTracking(form.getProductId(), trackingPerson, form.getType());
			return responseFactory.success(tracking);
		}
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
