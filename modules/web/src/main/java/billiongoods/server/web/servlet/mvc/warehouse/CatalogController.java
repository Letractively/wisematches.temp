package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.search.Order;
import billiongoods.core.search.Orders;
import billiongoods.core.search.Range;
import billiongoods.server.services.showcase.*;
import billiongoods.server.warehouse.Article;
import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.ArticleListener;
import billiongoods.server.warehouse.ArticleManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/catalog")
public class CatalogController extends AbstractController implements InitializingBean {
	private ArticleManager articleManager;
	private ShowcaseManager showcaseManager;

	private Showcase showcase;
	private final Map<ShowcaseItem, List<ArticleDescription>> showcaseCache = new HashMap<>();

	private final TheCatalogRefreshListener catalogRefreshListener = new TheCatalogRefreshListener();

	private static final Range RANGE = Range.limit(8);
	private static final Orders ORDERS = Orders.of(Order.desc("registrationDate"));

	public CatalogController() {
		super(false, true);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@RequestMapping(value = {"", "/"})
	public String showRootCategory(Model model) {
		if (showcase == null) {
			initializeShowcaseCache();
		}

		hideWhereabouts(model);

		model.addAttribute("showcase", showcase);
		model.addAttribute("showcaseCache", showcaseCache);

		return "/content/warehouse/catalog";
	}

	private void initializeShowcaseCache() {
		invalidateShowcaseCache();

		if (articleManager != null && showcaseManager != null) {
			showcase = showcaseManager.getShowcase();
			for (ShowcaseGroup showcaseGroup : showcase.getShowcaseGroups()) {
				for (ShowcaseItem item : showcaseGroup.getShowcaseItems()) {
					showcaseCache.put(item, articleManager.searchEntities(item.getArticleContext(), ORDERS, RANGE));
				}
			}
		}
	}

	private void invalidateShowcaseCache() {
		showcase = null;
		showcaseCache.clear();
	}

	@Autowired
	public void setArticleManager(ArticleManager articleManager) {
		if (this.articleManager != null) {
			this.articleManager.removeArticleListener(catalogRefreshListener);
		}

		this.articleManager = articleManager;

		if (this.articleManager != null) {
			this.articleManager.addArticleListener(catalogRefreshListener);
		}
	}

	@Autowired
	public void setShowcaseManager(ShowcaseManager showcaseManager) {
		if (this.showcaseManager != null) {
			this.showcaseManager.removeShowcaseListener(catalogRefreshListener);
		}

		this.showcaseManager = showcaseManager;

		if (this.showcaseManager != null) {
			this.showcaseManager.addShowcaseListener(catalogRefreshListener);
		}
	}

	private final class TheCatalogRefreshListener implements ArticleListener, ShowcaseListener {
		private TheCatalogRefreshListener() {
		}

		@Override
		public void articleCreated(Article article) {
			invalidateShowcaseCache();
		}

		@Override
		public void articleUpdated(Article article) {
			invalidateShowcaseCache();
		}

		@Override
		public void articleRemoved(Article article) {
			invalidateShowcaseCache();
		}

		@Override
		public void showcaseInvalidated(Showcase showcase) {
			invalidateShowcaseCache();
		}
	}
}
