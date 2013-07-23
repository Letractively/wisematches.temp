package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.warehouse.Catalog;
import billiongoods.server.warehouse.CategoryManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class WarehouseController extends AbstractController {
	protected CategoryManager categoryManager;

	public WarehouseController() {
	}

	@ModelAttribute("catalog")
	public Catalog getCatalog() {
		return categoryManager.getCatalog();
	}

	@Autowired
	public void setCategoryManager(CategoryManager categoryManager) {
		this.categoryManager = categoryManager;
	}
}
