package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.search.Order;
import billiongoods.core.search.Orders;
import billiongoods.core.search.Range;
import billiongoods.server.services.advise.ProductAdviseManager;
import billiongoods.server.services.showcase.*;
import billiongoods.server.warehouse.Product;
import billiongoods.server.warehouse.ProductListener;
import billiongoods.server.warehouse.ProductManager;
import billiongoods.server.warehouse.ProductPreview;
import billiongoods.server.web.servlet.mvc.AbstractController;
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
public class CatalogController extends AbstractController {
	private ProductManager productManager;
	private ShowcaseManager showcaseManager;
	private ProductAdviseManager adviseManager;

	private Showcase showcase;
	private final Map<ShowcaseItem, List<ProductPreview>> showcaseCache = new HashMap<>();

	private final TheCatalogRefreshListener catalogRefreshListener = new TheCatalogRefreshListener();

	private static final Range RANGE = Range.limit(8);
	private static final Orders ORDERS = Orders.of(Order.desc("registrationDate"));

	public CatalogController() {
		super(false, true);
	}

	@RequestMapping(value = {"", "/"})
	public String showRootCategory(Model model) {
		if (showcase == null) {
			initializeShowcaseCache();
		}

		hideWhereabouts(model);

		model.addAttribute("showcase", showcase);
		model.addAttribute("showcaseCache", showcaseCache);
		model.addAttribute("recommendations", adviseManager.getRecommendations(null, 8));

		return "/content/warehouse/catalog";
	}

	private void initializeShowcaseCache() {
		invalidateShowcaseCache();

		if (productManager != null && showcaseManager != null) {
			showcase = showcaseManager.getShowcase();
			for (ShowcaseGroup showcaseGroup : showcase.getShowcaseGroups()) {
				for (ShowcaseItem item : showcaseGroup.getShowcaseItems()) {
					showcaseCache.put(item, productManager.searchEntities(item.getProductContext(), null, RANGE, ORDERS));
				}
			}
		}
	}

	private void invalidateShowcaseCache() {
		showcase = null;
		showcaseCache.clear();
	}

	@Autowired
	public void setProductManager(ProductManager productManager) {
		if (this.productManager != null) {
			this.productManager.removeProductListener(catalogRefreshListener);
		}

		this.productManager = productManager;

		if (this.productManager != null) {
			this.productManager.addProductListener(catalogRefreshListener);
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

	@Autowired
	public void setAdviseManager(ProductAdviseManager adviseManager) {
		this.adviseManager = adviseManager;
	}

	private final class TheCatalogRefreshListener implements ProductListener, ShowcaseListener {
		private TheCatalogRefreshListener() {
		}

		@Override
		public void productCreated(Product product) {
			invalidateShowcaseCache();
		}

		@Override
		public void productUpdated(Product product) {
			invalidateShowcaseCache();
		}

		@Override
		public void productRemoved(Product product) {
			invalidateShowcaseCache();
		}

		@Override
		public void showcaseInvalidated(Showcase showcase) {
			invalidateShowcaseCache();
		}
	}
}
