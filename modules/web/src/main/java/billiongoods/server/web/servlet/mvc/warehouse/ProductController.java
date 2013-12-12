package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.Member;
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

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/product")
public class ProductController extends AbstractController {
	private ProductManager productManager;
	private ProductTrackingManager trackingManager;
	private RelationshipManager relationshipManager;

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

		final Set<ProductPreview> mode = new HashSet<>();
		final Set<ProductPreview> similar = new HashSet<>();
		final List<Group> groups = relationshipManager.getGroups(product.getId());
		for (Group group : groups) {
			for (ProductPreview preview : group.getProductPreviews()) {
				if (ProductContext.ACTIVE_ONLY.contains(preview.getState())) {
					if (group.getType() == GroupType.MODE) {
						mode.add(preview);
					} else if (group.getType() == GroupType.SIMILAR) {
						similar.add(preview);
					}
				}
			}
		}

		final Set<ProductPreview> accessories = new HashSet<>();
		final List<Relationship> relationships = relationshipManager.getRelationships(product.getId());
		for (Relationship relationship : relationships) {
			if (relationship.getType() == RelationshipType.ACCESSORIES) {
				for (ProductPreview preview : relationship.getProductPreviews()) {
					if (ProductContext.ACTIVE_ONLY.contains(preview.getState())) {
						accessories.add(preview);
					}
				}
			}
		}

		mode.remove(product);
		similar.remove(product);
		similar.removeAll(mode); // remove all modes
		accessories.remove(product);

		final Member member = getMember();
		final StockState stockState = product.getStockInfo().getStockState();
		if (member != null && (stockState != StockState.IN_STOCK && stockState != StockState.LIMITED_NUMBER)) {
			model.addAttribute("tracking", trackingManager.getTracking(product.getId(), member));
		}

		model.addAttribute("mode", mode);
		model.addAttribute("similar", similar);
		model.addAttribute("accessories", accessories);

		model.addAttribute("groups", groups);
		model.addAttribute("relationships", relationships);

		hideNavigation(model);

		return "/content/warehouse/product";
	}

	@RequestMapping("/tracking.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse changeTrackingState(@RequestBody ProductTrackingForm form, Locale locale) {
		if (form.getProductId() == null) {
			return responseFactory.failure("product.subscribe.error.unknown", locale);
		}

		final ProductPreview preview = productManager.getPreview(form.getProductId());
		if (preview == null) {
			return responseFactory.failure("product.subscribe.error.unknown", locale);
		}

		return processSubscription(form, locale);
	}

	private ServiceResponse processSubscription(ProductTrackingForm form, Locale locale) {
		TrackingContext context;
		final Member member = getMember();
		if (member != null) {
			context = new TrackingContext(form.getProductId(), member, form.getType());
		} else {
			if (form.getEmail() == null || form.getEmail().isEmpty()) {
				return responseFactory.failure("product.subscribe.error.email", locale);
			}
			context = new TrackingContext(form.getProductId(), form.getEmail(), form.getType());
		}

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
			if (member != null) {
				tracking = trackingManager.createTracking(form.getProductId(), member, form.getType());
			} else {
				tracking = trackingManager.createTracking(form.getProductId(), form.getEmail(), form.getType());
			}
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
}
