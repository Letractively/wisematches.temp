package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.warehouse.Article;
import billiongoods.server.warehouse.ArticleManager;
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
@RequestMapping("/warehouse/article")
public class ArticleController extends AbstractController {
	private ArticleManager articleManager;

	public ArticleController() {
	}

	@RequestMapping("/{articleId}")
	public String showSubCategory(@PathVariable("articleId") Integer articleId, Model model) {
		final Article article = articleManager.getArticle(articleId);
		if (article == null) {
			throw new UnknownEntityException(articleId, "article");
		}

		setTitle(model, article.getName() + " - " + article.getCategory().getName());

		model.addAttribute("article", article);
		model.addAttribute("category", article.getCategory());

		hideNavigation(model);

		return "/content/warehouse/article";
	}

	@Autowired
	public void setArticleManager(ArticleManager articleManager) {
		this.articleManager = articleManager;
	}
}
