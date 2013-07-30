package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.warehouse.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import billiongoods.server.web.servlet.mvc.warehouse.form.ItemsTableForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
	public String showSubCategory(@PathVariable("categoryId") Integer categoryId, Model model, @ModelAttribute("itemsTableForm") ItemsTableForm tableForm) {
		final Category category = categoryManager.getCategory(categoryId);
		if (category == null) {
			throw new UnknownEntityException(categoryId, "category");
		}

		setTitle(model, category.getName());

		final ArticleContext context = new ArticleContext(category);

		final int totalCount = articleManager.getTotalCount(context);
		final List<ArticleDescription> articles = articleManager.searchEntities(context, null, tableForm.createRange());

		tableForm.setTotalCount(totalCount);

		model.addAttribute("category", category);
		model.addAttribute("articles", articles);

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