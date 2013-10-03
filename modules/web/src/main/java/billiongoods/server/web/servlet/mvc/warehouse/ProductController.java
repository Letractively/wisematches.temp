package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.warehouse.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/product")
public class ProductController extends AbstractController {
	private ProductManager productManager;
	private RelationshipManager relationshipManager;

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

	private String getProductPostfix(Product product) {
		return "-" + product.getName().trim().replaceAll(" ", "-").toLowerCase();
	}

	@Autowired
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}

	@Autowired
	public void setRelationshipManager(RelationshipManager relationshipManager) {
		this.relationshipManager = relationshipManager;
	}
}
