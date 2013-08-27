package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.search.Order;
import billiongoods.core.search.Orders;
import billiongoods.core.search.Range;
import billiongoods.server.warehouse.ArticleContext;
import billiongoods.server.warehouse.ArticleManager;
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

	public CatalogController() {
	}

	@RequestMapping(value = {"", "/"})
	public String showRootCategory(Model model) {
		hideWarehouse(model);

//		model.addAttribute("topSelling", articleManager.searchEntities(null, Orders.of(Order.desc("soldCount")), Range.limit(8));

		final Range limit = Range.limit(itemsInGroup);
		final Orders orders = Orders.of(Order.desc("registrationDate"));
		final ArticleContext context = new ArticleContext(null, true, null, true);

		model.addAttribute("newArrival", articleManager.searchEntities(context, orders, limit));
		return "/content/warehouse/catalog";
	}

	@Autowired
	public void setArticleManager(ArticleManager articleManager) {
		this.articleManager = articleManager;
	}
}
