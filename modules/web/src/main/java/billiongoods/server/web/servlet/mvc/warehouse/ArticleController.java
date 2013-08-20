package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.warehouse.Article;
import billiongoods.server.warehouse.ArticleManager;
import billiongoods.server.warehouse.RelationshipManager;
import billiongoods.server.warehouse.Relationships;
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
	private RelationshipManager relationshipManager;

	public ArticleController() {
	}

	@RequestMapping("/{articleId}")
	public String showSubCategory(@PathVariable("articleId") String articleId, Model model) {
		final Article article;
		if (articleId.startsWith("SKU")) {
			article = articleManager.getArticle(articleId);
		} else {
			article = articleManager.getArticle(Integer.decode(articleId));
		}
		if (article == null) {
			throw new UnknownEntityException(articleId, "article");
		}

		setTitle(model, article.getName() + " - " + article.getCategory().getName());

		model.addAttribute("article", article);
		model.addAttribute("category", article.getCategory());
		model.addAttribute("relationships", new Relationships(relationshipManager.getRelationships(article.getId())));

		hideNavigation(model);

		return "/content/warehouse/article";
	}

	@Autowired
	public void setArticleManager(ArticleManager articleManager) {
		this.articleManager = articleManager;
	}

	@Autowired
	public void setRelationshipManager(RelationshipManager relationshipManager) {
		this.relationshipManager = relationshipManager;
	}
}
