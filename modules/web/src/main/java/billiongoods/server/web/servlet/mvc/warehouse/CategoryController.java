package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.search.Orders;
import billiongoods.core.search.Range;
import billiongoods.server.MessageFormatter;
import billiongoods.server.services.advise.ProductAdviseManager;
import billiongoods.server.warehouse.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import billiongoods.server.web.servlet.mvc.warehouse.form.ProductsPageableForm;
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

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse")
public class CategoryController extends AbstractController {
	private ProductManager productManager;
	private ProductAdviseManager adviseManager;

	private static final Pattern PRODUCT_SKU_PATTER = Pattern.compile("^SKU(\\d{6})$");

	private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.CategoryController");

	public CategoryController() {
	}

	@RequestMapping("/category/{categoryUri}")
	public String showCategory(@PathVariable("categoryUri") String categoryUri,
							   @ModelAttribute("pageableForm") ProductsPageableForm pageableForm, Model model) {
		final Integer categoryId;
		final int i = categoryUri.lastIndexOf("-");
		if (i < 0) {
			categoryId = Integer.decode(categoryUri);
		} else {
			categoryId = Integer.decode(categoryUri.substring(i + 1));
		}

		final Category category = categoryManager.getCategory(categoryId);
		if (category == null) {
			throw new UnknownEntityException(categoryId, "category");
		}

		final String symbolicUri = category.getSymbolicUri();
		if (symbolicUri != null && !symbolicUri.isEmpty() && !symbolicUri.equals(categoryUri)) {
			return "redirect:/warehouse/category/" + symbolicUri;
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
								  @ModelAttribute("pageableForm") ProductsPageableForm pageableForm, Model model) {
		setTitle(model, "Новые поступления - Бесплатная доставка");
		pageableForm.setSort(SortingType.ARRIVAL_DATE.getCode());
		prepareProducts(categoryId, pageableForm, model, true);
		return "/content/warehouse/category";
	}

	@RequestMapping("/topselling")
	public String showTopSellers(@RequestParam(value = "category", required = false) Integer categoryId,
								 @ModelAttribute("pageableForm") ProductsPageableForm pageableForm, Model model) {
		setTitle(model, "Лучшие продажи - Бесплатная доставка");
		pageableForm.setSort(SortingType.BESTSELLING.getCode());
		prepareProducts(categoryId, pageableForm, model, false);
		pageableForm.disableSorting();
		return "/content/warehouse/category";
	}

	@RequestMapping("/search")
	public String searchProducts(@RequestParam(value = "category", required = false) Integer categoryId,
								 @ModelAttribute("pageableForm") ProductsPageableForm pageableForm, Model model) {
		String query = pageableForm.getQuery() == null ? "" : pageableForm.getQuery().trim();

		final Integer pid = MessageFormatter.extractProductId(query);
		if (pid != null) {
			return "redirect:/warehouse/product/" + pid;
		}

		if (hasRole("moderator") && PRODUCT_SKU_PATTER.matcher(query).matches()) {
			final Integer id = productManager.searchBySku(query);
			if (id != null) {
				return "redirect:/warehouse/product/" + id;
			}
		}

		setTitle(model, "Результат поска по запросу");
		prepareProducts(categoryId, pageableForm, model, false);
		return "/content/warehouse/category";
	}

	private void prepareProducts(Integer categoryId, ProductsPageableForm pageableForm, Model model, boolean arrivals) {
		Category category = null;
		if (categoryId != null && categoryId != 0) {
			category = categoryManager.getCategory(categoryId);
		} else {
			model.addAttribute("showCategory", Boolean.TRUE);
		}

		final ProductFilter filter = prepareFilter(pageableForm.getFilter());
		final EnumSet<ProductState> productStates = hasRole("moderator") ? ProductContext.NOT_REMOVED : ProductContext.VISIBLE;

		final ProductContext context = new ProductContext(category, true, pageableForm.getQuery(), arrivals, productStates, null);
		final Filtering filtering = productManager.getFilteringAbility(context, filter);

		pageableForm.initialize(filtering.getTotalCount(), filtering.getFilteredCount());

		final Range range = pageableForm.getRange();
		final Orders orders = pageableForm.getOrders();
		final List<ProductPreview> products = productManager.searchEntities(context, filter, range, orders);

		model.addAttribute("category", category);
		model.addAttribute("products", products);

		model.addAttribute("filter", filter);
		model.addAttribute("filtering", filtering);

		model.addAttribute("recommendations", adviseManager.getRecommendations(category, 5));
	}

	private Map<String, Object> decodeFilter(String filter) throws UnsupportedEncodingException {
		final Map<String, Object> res = new HashMap<>();
		final String decode = URLDecoder.decode(filter, "UTF-8");

		final String[] split = decode.split("&");
		for (String s : split) {
			final String[] split1 = s.split("=");
			final String name = split1[0];
			final String value = split1.length > 1 ? split1[1] : null;

			Object o = res.get(name);
			if (o != null) {
				if (o.getClass().isArray()) {
					final String[] arr = (String[]) o;
					final String[] ar = new String[arr.length + 1];
					System.arraycopy(arr, 0, ar, 0, arr.length);
					ar[arr.length] = value;
					o = ar;
				} else {
					o = new String[]{(String) o, value};
				}
			} else {
				o = value;
			}
			res.put(name, o);
		}
		return res;
	}

	private ProductFilter prepareFilter(String filter) {
		if (filter == null || filter.isEmpty()) {
			return null;
		}
		try {
			Double minPrice = null;
			Double maxPrice = null;

			final Map<Attribute, FilteringValue> res = new HashMap<>();
			final Map<String, Object> stringObjectMap = decodeFilter(filter);
			for (Map.Entry<String, Object> entry : stringObjectMap.entrySet()) {
				final String name = entry.getKey();
				final Object value = entry.getValue();
				if (name != null && !name.isEmpty() && value != null) {
					switch (name) {
						case "minPrice":
							try {
								minPrice = Double.valueOf((String) value);
							} catch (NumberFormatException ignore) {
							}
							break;
						case "maxPrice":
							try {
								maxPrice = Double.valueOf((String) value);
							} catch (NumberFormatException ignore) {
							}
							break;
						default:
							final Attribute attr = attributeManager.getAttribute(Integer.parseInt(name));
							if (attr != null) {
								final FilteringValue filterValue;
								final AttributeType type = attr.getAttributeType();
								if (type == AttributeType.INTEGER) {
									final String[] a = (String[]) value;
									final BigDecimal min = a[0].trim().isEmpty() ? null : new BigDecimal(a[0]);
									final BigDecimal max = a[1].trim().isEmpty() ? null : new BigDecimal(a[1]);
									filterValue = new FilteringValue.Range(min, max);
								} else if (type == AttributeType.BOOLEAN) {
									filterValue = new FilteringValue.Bool(Boolean.valueOf((String) value));
								} else {
									final Set<String> v = new HashSet<>();
									if (value.getClass().isArray()) {
										Collections.addAll(v, (String[]) value);
									} else {
										v.add((String) value);
									}
									filterValue = new FilteringValue.Enum(v);
								}
								res.put(attr, filterValue);
							}
							break;
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

	@Autowired
	public void setAdviseManager(ProductAdviseManager adviseManager) {
		this.adviseManager = adviseManager;
	}
}