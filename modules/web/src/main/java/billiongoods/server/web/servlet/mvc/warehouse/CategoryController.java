package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.search.Orders;
import billiongoods.core.search.Range;
import billiongoods.server.warehouse.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import billiongoods.server.web.servlet.mvc.warehouse.form.ItemSortType;
import billiongoods.server.web.servlet.mvc.warehouse.form.ItemsTableForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse")
public class CategoryController extends AbstractController {
	private ProductManager productManager;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.CategoryController");

	public CategoryController() {
	}

	@RequestMapping("/category/{categoryId}")
	public String showCategory(@PathVariable("categoryId") Integer categoryId,
							   @RequestParam(value = "filter", required = false) String filter,
							   @ModelAttribute("itemsTableForm") ItemsTableForm tableForm, Model model) {
		final Category category = categoryManager.getCategory(categoryId);
		if (category == null) {
			throw new UnknownEntityException(categoryId, "category");
		}

		final StringBuilder sb = new StringBuilder();
		for (Category ct : category.getGenealogy()) {
			sb.append(ct.getName());
			sb.append(" - ");
		}
		sb.append(category.getName());
		setTitle(model, sb.toString());

		prepareProducts(categoryId, model, tableForm, filter, false);
		return "/content/warehouse/category";
	}

	@RequestMapping("/arrivals")
	public String showNewArrivals(@RequestParam(value = "c", required = false) Integer categoryId,
								  Model model, @ModelAttribute("itemsTableForm") ItemsTableForm tableForm) {
		tableForm.setItemSortType(ItemSortType.ARRIVAL_DATE);
		prepareProducts(categoryId, model, tableForm, null, true);
		return "/content/warehouse/category";
	}

	@RequestMapping("/topselling")
	public String showTopSellers(@RequestParam(value = "c", required = false) Integer categoryId,
								 Model model, @ModelAttribute("itemsTableForm") ItemsTableForm tableForm) {
		tableForm.setItemSortType(ItemSortType.BESTSELLING);
		prepareProducts(categoryId, model, tableForm, null, false);
		tableForm.disableSorting();
		return "/content/warehouse/category";
	}

	private void prepareProducts(Integer categoryId, Model model, ItemsTableForm tableForm, String filterForm, boolean arrivals) {
		Category category = null;
		if (categoryId != null) {
			category = categoryManager.getCategory(categoryId);
		}

		final ProductFilter filter = prepareFilter(filterForm);
		final boolean inactive = tableForm.isInactive() || hasRole("moderator");
		final EnumSet<ProductState> productStates = inactive ? ProductContext.NOT_REMOVED : ProductContext.VISIBLE;

		final ProductContext context = new ProductContext(category, true, tableForm.getQuery(), arrivals, productStates);
		tableForm.validateForm(productManager.getTotalCount(context));

		final FilteringAbility filtering = productManager.getFilteringAbility(context);

		final Range range = tableForm.createRange();
		final Orders orders = Orders.of(tableForm.getItemSortType().getOrder());
		final List<ProductDescription> products = productManager.searchEntities(context, filter, range, orders);

		model.addAttribute("category", category);
		model.addAttribute("products", products);

		model.addAttribute("filter", filter);
		model.addAttribute("filtering", filtering);
	}

	private ProductFilter prepareFilter(String filter) {
		if (filter == null) {
			return null;
		}
		try {
			final Map<Attribute, List<String>> res = new HashMap<>();

			final String decode = URLDecoder.decode(filter, "UTF-8");
			final String[] split = decode.split("&");
			for (String s : split) {
				final String[] split1 = s.split("=");

				final Attribute attr = attributeManager.getAttribute(Integer.parseInt(split1[0]));
				if (attr != null) {
					List<String> strings = res.get(attr);
					if (strings == null) {
						strings = new ArrayList<>();
						res.put(attr, strings);
					}

					if (split1.length > 1) {
						final String value = split1[1];
						if (value != null) {
							strings.add(value);
						}
					} else {
						strings.add("");
					}
				}
			}
			return new ProductFilter(res);
		} catch (Exception ex) {
			log.error("ProductFilter can't be processed: " + filter, ex);
			return null;
		}
	}

	@Autowired
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}
}