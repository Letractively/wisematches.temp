package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.search.Order;
import billiongoods.core.search.Orders;
import billiongoods.core.search.Range;
import billiongoods.server.warehouse.ArticleContext;
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
	private int itemsInGroup = 16;

	private ArticleManager articleManager;
	private CategoryManager categoryManager;

	public CatalogController() {
	}

	@RequestMapping(value = {"", "/"})
	public String showRootCategory(Model model) {
		hideWarehouse(model);

//		model.addAttribute("topSelling", articleManager.searchEntities(null, Orders.of(Order.desc("soldCount")), Range.limit(8));

		model.addAttribute("newArrival", articleManager.searchEntities(new ArticleContext(null, true, true), Orders.of(Order.desc("registrationDate")), Range.limit(itemsInGroup)));

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
