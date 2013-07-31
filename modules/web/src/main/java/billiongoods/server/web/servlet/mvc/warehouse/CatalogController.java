package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.warehouse.ArticleManager;
import billiongoods.server.warehouse.CategoryManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/catalog")
public class CatalogController extends AbstractController {
	private ArticleManager articleManager;
	private CategoryManager categoryManager;

	public CatalogController() {
	}

	@RequestMapping(value = {"", "/"})
	public String showRootCategory(Model model) {
		hideWarehouse(model);

		model.addAttribute("catalog", categoryManager.getCatalog());
		return "/content/warehouse/catalog";
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
