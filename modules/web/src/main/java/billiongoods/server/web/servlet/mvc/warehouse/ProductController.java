package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.Member;
import billiongoods.server.services.tracking.ProductTrackingManager;
import billiongoods.server.services.tracking.TrackingPerson;
import billiongoods.server.warehouse.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/product")
public class ProductController extends AbstractController {
	private ProductManager productManager;
	private RelationshipManager relationshipManager;
	private ProductTrackingManager trackingManager;

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
			model.addAttribute("registeredTracking", trackingManager.containsTracking(product.getId(), TrackingPerson.of(member)));
		}

		model.addAttribute("mode", mode);
		model.addAttribute("similar", similar);
		model.addAttribute("accessories", accessories);

		model.addAttribute("groups", groups);
		model.addAttribute("relationships", relationships);

		hideNavigation(model);

		return "/content/warehouse/product";
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
