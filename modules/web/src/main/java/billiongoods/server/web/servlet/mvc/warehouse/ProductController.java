package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.Personality;
import billiongoods.server.services.notify.NotificationException;
import billiongoods.server.services.notify.NotificationService;
import billiongoods.server.services.notify.Recipient;
import billiongoods.server.services.notify.Sender;
import billiongoods.server.services.tracking.ProductTracking;
import billiongoods.server.services.tracking.ProductTrackingManager;
import billiongoods.server.services.tracking.TrackingContext;
import billiongoods.server.warehouse.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import billiongoods.server.web.servlet.mvc.warehouse.form.ProductTrackingForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.TrackingChangeType;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/product")
public class ProductController extends AbstractController {
	private ProductManager productManager;
	private ProductTrackingManager trackingManager;
	private RelationshipManager relationshipManager;
	private NotificationService notificationService;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.ProductController");

	public ProductController() {
	}

	@RequestMapping("/{pid:\\d+}{name:.*}")
	public String showProduct(@PathVariable("pid") String productId,
							  @PathVariable("name") String name,
							  Model model) {
		final Product product;
		try {
			product = productManager.getProduct(Integer.decode(productId));
		} catch (NumberFormatException ex) {
			throw new UnknownEntityException(productId, "product");
		}

		if (product == null) {
			throw new UnknownEntityException(productId, "product");
		}

		if (!ProductContext.VISIBLE.contains(product.getState()) && !hasRole("moderator")) {
			throw new UnknownEntityException(productId, "product");
		}

		final Category category = categoryManager.getCategory(product.getCategoryId());

		setTitle(model, product.getName() + " - " + category.getName());

		model.addAttribute("product", product);
		model.addAttribute("category", category);

		final List<ProductDescription> related = new ArrayList<>();
		final List<Group> groups = relationshipManager.getGroups(product.getId());
		for (Group group : groups) {
			related.addAll(group.getDescriptions());
		}

		final List<ProductDescription> accessories = new ArrayList<>();
		final List<Relationship> relationships = relationshipManager.getRelationships(product.getId());
		for (Relationship relationship : relationships) {
			if (relationship.getType() == RelationshipType.ACCESSORIES) {
				accessories.addAll(relationship.getDescriptions());
			}
		}
		related.remove(product);
		accessories.remove(product);

		model.addAttribute("related", related);
		model.addAttribute("accessories", accessories);

		hideNavigation(model);

		return "/content/warehouse/product";
	}

	@RequestMapping("/tracking.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse changeTrackingState(@RequestBody ProductTrackingForm form, Locale locale) {
		final Personality principal = getPrincipal();

		if (form.getProductId() == null) {
			return responseFactory.failure("product.subscribe.error.unknown", locale);
		}

		final ProductDescription description = productManager.getDescription(form.getProductId());
		if (description == null) {
			return responseFactory.failure("product.subscribe.error.unknown", locale);
		}

		try {
			if (form.getChangeType() == TrackingChangeType.SUBSCRIBE) {
				notificationService.raiseNotification(
						"system." + form.getType().name().toLowerCase(),
						Recipient.MONITORING, Sender.SERVER, description);
			}
		} catch (NotificationException e) {
			log.error("Product description request can't be send: " + form, e);
			return responseFactory.failure("product.subscribe.error.system", locale);
		}

		if (form.getEmail() != null && !form.getEmail().isEmpty()) {
			return processSubscription(form, locale, principal);
		}
		return responseFactory.success();
	}

	private ServiceResponse processSubscription(ProductTrackingForm form, Locale locale, Personality principal) {
		TrackingContext context;
//		if (principal instanceof Member) {
//			context = new TrackingContext(form.getProductId(), principal.getId(), form.getType());
//		} else {
		context = new TrackingContext(form.getProductId(), form.getEmail(), form.getType());
//		}

		final List<ProductTracking> trackers = trackingManager.searchEntities(context, null, null, null);
		if (form.getChangeType() == TrackingChangeType.UNSUBSCRIBE) {
			for (ProductTracking tracker : trackers) {
				trackingManager.removeTracking(tracker.getId());
			}
		} else if (form.getChangeType() == TrackingChangeType.SUBSCRIBE) {
			if (!trackers.isEmpty()) {
				return responseFactory.failure("product.subscribe.error.subscribed", locale);
			}

			ProductTracking tracking;
//			if (principal instanceof Member) {
//				tracking = trackingManager.createTracking(form.getProductId(), principal, form.getType());
//			} else {
			tracking = trackingManager.createTracking(form.getProductId(), form.getEmail(), form.getType());
//			}
			return responseFactory.success(tracking);
		}
		return responseFactory.failure("product.subscribe.error.incorrect", locale);
	}

	private String getProductPostfix(Product product) {
		return "-" + product.getName().trim().replaceAll(" ", "-").toLowerCase();
	}

	@Autowired
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	@Autowired
	public void setTrackingManager(ProductTrackingManager trackingManager) {
		this.trackingManager = trackingManager;
	}

	@Autowired
	public void setRelationshipManager(RelationshipManager relationshipManager) {
		this.relationshipManager = relationshipManager;
	}

	@Autowired
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}
}
