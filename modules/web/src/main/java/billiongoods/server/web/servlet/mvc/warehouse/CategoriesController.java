package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.search.Range;
import billiongoods.server.warehouse.ArticleContext;
import billiongoods.server.warehouse.ArticleManager;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.CategoryManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/category")
public class CategoriesController extends AbstractController {
	private ArticleManager articleManager;
	private CategoryManager categoryManager;

	public CategoriesController() {
	}

	@RequestMapping("/{categoryId}")
	public String showSubCategory(@PathVariable("categoryId") Integer categoryId, Model model) {
		final Category category = categoryManager.getCategory(categoryId);
		if (category == null) {
			throw new UnknownEntityException(categoryId, "category");
		}

		setTitle(model, category.getName());

		model.addAttribute("category", category);

		// TODO: be must parameters more here
		model.addAttribute("articles", articleManager.searchEntities(new ArticleContext(category), null, Range.limit(36)));

		return "/content/warehouse/category";
	}

	@Autowired
	public void setArticleManager(ArticleManager articleManager) {
		this.articleManager = articleManager;
	}

	@Autowired
	public void setCategoryManager(CategoryManager categoryManager) {
		this.categoryManager = categoryManager;
	}
}