package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.search.Orders;
import billiongoods.core.search.Range;
import billiongoods.server.MessageFormatter;
import billiongoods.server.warehouse.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import billiongoods.server.web.servlet.mvc.warehouse.form.PageableForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.SortingType;
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
							   @ModelAttribute("pageableForm") PageableForm pageableForm, Model model) {
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

		pageableForm.setCategory(null);

		prepareProducts(categoryId, pageableForm, model, false);
		return "/content/warehouse/category";
	}

	@RequestMapping("/arrivals")
	public String showNewArrivals(@RequestParam(value = "category", required = false) Integer categoryId,
								  @ModelAttribute("pageableForm") PageableForm pageableForm, Model model) {
		setTitle(model, "Новые поступления - Бесплатная доставка");
		pageableForm.setSort(SortingType.ARRIVAL_DATE.getCode());
		prepareProducts(categoryId, pageableForm, model, true);
		return "/content/warehouse/category";
	}

	@RequestMapping("/topselling")
	public String showTopSellers(@RequestParam(value = "category", required = false) Integer categoryId,
								 @ModelAttribute("pageableForm") PageableForm pageableForm, Model model) {
		setTitle(model, "Лучшие продажи - Бесплатная доставка");
		pageableForm.setSort(SortingType.BESTSELLING.getCode());
		prepareProducts(categoryId, pageableForm, model, false);
		pageableForm.disableSorting();
		return "/content/warehouse/category";
	}

	@RequestMapping("/search")
	public String searchProducts(@RequestParam(value = "category", required = false) Integer categoryId,
								 @ModelAttribute("pageableForm") PageableForm pageableForm, Model model) {
		String query = pageableForm.getQuery();

		final Integer pid = MessageFormatter.extractProductId(query);
		if (pid != null) {
			return "redirect:/warehouse/product/" + pid;
		}

		setTitle(model, "Результат поска по запросу");
		prepareProducts(categoryId, pageableForm, model, false);
		return "/content/warehouse/category";
	}

	private void prepareProducts(Integer categoryId, PageableForm pageableForm, Model model, boolean arrivals) {
		Category category = null;
		if (categoryId != null && categoryId != 0) {
			category = categoryManager.getCategory(categoryId);
		} else {
			model.addAttribute("showCategory", Boolean.TRUE);
		}

		final ProductFilter filter = prepareFilter(pageableForm.getFilter());
		final EnumSet<ProductState> productStates = hasRole("moderator") ? ProductContext.NOT_REMOVED : ProductContext.VISIBLE;

		final ProductContext context = new ProductContext(category, true, pageableForm.getQuery(), arrivals, productStates);
		pageableForm.initialize(productManager.getTotalCount(context, filter));

		final FilteringAbility filtering = productManager.getFilteringAbility(context);

		final Range range = pageableForm.getRange();
		final Orders orders = pageableForm.getOrders();
		final List<ProductDescription> products = productManager.searchEntities(context, filter, range, orders);

		model.addAttribute("category", category);
		model.addAttribute("products", products);

		model.addAttribute("filter", filter);
		model.addAttribute("filtering", filtering);
	}

	private ProductFilter prepareFilter(String filter) {
		if (filter == null || filter.isEmpty()) {
			return null;
		}
		try {
			final Map<Attribute, List<String>> res = new HashMap<>();

			Double minPrice = null;
			Double maxPrice = null;

			final String decode = URLDecoder.decode(filter, "UTF-8");
			final String[] split = decode.split("&");
			for (String s : split) {
				final String[] split1 = s.split("=");
				final String name = split1[0];
				final String value = split1.length > 1 ? split1[1] : null;

				if (name != null && !name.isEmpty()) {
					if ("minPrice".equals(name)) {
						try {
							if (value != null) {
								minPrice = Double.valueOf(value);
							}
						} catch (NumberFormatException ignore) {
						}
					} else if ("maxPrice".equals(name)) {
						try {
							if (value != null) {
								maxPrice = Double.valueOf(value);
							}
						} catch (NumberFormatException ignore) {
						}
					} else {
						final Attribute attr = attributeManager.getAttribute(Integer.parseInt(name));
						if (attr != null) {
							List<String> strings = res.get(attr);
							if (strings == null) {
								strings = new ArrayList<>();
								res.put(attr, strings);
							}

							if (split1.length > 1) {
								if (value != null) {
									strings.add(value);
								}
							} else {
								strings.add("");
							}
						}
					}
				}
			}
			return new ProductFilter(minPrice, maxPrice, res);
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