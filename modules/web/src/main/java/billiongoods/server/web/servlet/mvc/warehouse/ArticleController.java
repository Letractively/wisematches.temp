package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.warehouse.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

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

		final Category category = categoryManager.getCategory(article.getCategoryId());

		setTitle(model, article.getName() + " - " + category.getName());

		model.addAttribute("article", article);
		model.addAttribute("category", category);

		final List<ArticleDescription> related = new ArrayList<>();
		final List<Group> groups = relationshipManager.getGroups(article.getId());
		for (Group group : groups) {
			related.addAll(group.getDescriptions());
		}

		final List<ArticleDescription> accessories = new ArrayList<>();
		final List<Relationship> relationships = relationshipManager.getRelationships(article.getId());
		for (Relationship relationship : relationships) {
			if (relationship.getType() == RelationshipType.ACCESSORIES) {
				accessories.addAll(relationship.getDescriptions());
			}
		}
		related.remove(article);
		accessories.remove(article);

		model.addAttribute("related", related);
		model.addAttribute("accessories", accessories);

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
