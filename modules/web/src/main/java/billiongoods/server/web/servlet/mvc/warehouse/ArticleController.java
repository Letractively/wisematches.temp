package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.warehouse.Category;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/article")
public class ArticleController extends WarehouseController {
	public ArticleController() {
	}

	@RequestMapping("/{articleId}")
	public String showSubCategory(@PathVariable("articleId") Integer articleId, Model model) {
/*
		final Category category = categoryManager.getCategory(categoryId);
		if (category == null) {
			throw new UnknownEntityException(categoryId, "category");
		}

*/
		setTitle(model, "Article will be here");

		final Category category = categoryManager.getCategory(20);

		model.addAttribute("article", articleId);
		model.addAttribute("category", category);

		return "/content/warehouse/article";
	}
}
