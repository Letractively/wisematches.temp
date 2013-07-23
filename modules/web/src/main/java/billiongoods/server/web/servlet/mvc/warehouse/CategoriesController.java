package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.warehouse.Category;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse")
public class CategoriesController extends WarehouseController {
	public CategoriesController() {
	}

	@RequestMapping(value = {"", "/", "/catalog"})
	public String showRootCategory() {
		return "/content/warehouse/category";
	}

	@RequestMapping("/category/{categoryId}")
	public String showSubCategory(@PathVariable("categoryId") Integer categoryId, Model model) {
		final Category category = categoryManager.getCategory(categoryId);
		if (category == null) {
			throw new UnknownEntityException(categoryId, "category");
		}

		model.addAttribute("title", category.getName());
		model.addAttribute("category", category);

		return "/content/warehouse/category";
	}
}
