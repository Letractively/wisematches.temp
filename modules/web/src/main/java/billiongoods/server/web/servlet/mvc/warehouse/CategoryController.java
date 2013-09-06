package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.search.Orders;
import billiongoods.core.search.Range;
import billiongoods.server.warehouse.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import billiongoods.server.web.servlet.mvc.warehouse.form.ItemSortType;
import billiongoods.server.web.servlet.mvc.warehouse.form.ItemsTableForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.EnumSet;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse")
public class CategoryController extends AbstractController {
	private ProductManager productManager;

	public CategoryController() {
	}

	@RequestMapping("/category/{categoryId}")
	public String showSubCategory(@PathVariable("categoryId") Integer categoryId, Model model, @ModelAttribute("itemsTableForm") ItemsTableForm tableForm) {
		final Category category = categoryManager.getCategory(categoryId);
		if (category == null) {
			throw new UnknownEntityException(categoryId, "category");
		}
		setTitle(model, category.getName());

		prepareProducts(categoryId, model, tableForm, false);
		return "/content/warehouse/category";
	}

	@RequestMapping("/arrivals")
	public String showNewArrivals(@RequestParam(value = "c", required = false) Integer categoryId,
								  Model model, @ModelAttribute("itemsTableForm") ItemsTableForm tableForm) {
		tableForm.setItemSortType(ItemSortType.ARRIVAL_DATE);
		prepareProducts(categoryId, model, tableForm, true);
		return "/content/warehouse/category";
	}

	@RequestMapping("/topselling")
	public String showTopSellers(@RequestParam(value = "c", required = false) Integer categoryId,
								 Model model, @ModelAttribute("itemsTableForm") ItemsTableForm tableForm) {
		tableForm.setItemSortType(ItemSortType.BESTSELLING);
		prepareProducts(categoryId, model, tableForm, false);
		tableForm.disableSorting();
		return "/content/warehouse/category";
	}

	private void prepareProducts(Integer categoryId, Model model, ItemsTableForm tableForm, boolean arrivals) {
		Category category = null;
		if (categoryId != null) {
			category = categoryManager.getCategory(categoryId);
		}

		final boolean inactive = tableForm.isInactive() || hasRole("moderator");
		final EnumSet<ProductState> productStates = inactive ? ProductContext.NOT_REMOVED : ProductContext.VISIBLE;

		final ProductContext context = new ProductContext(category, true, tableForm.getQuery(), arrivals, productStates);
		tableForm.validateForm(productManager.getTotalCount(context));

		final Range range = tableForm.createRange();
		final Orders orders = Orders.of(tableForm.getItemSortType().getOrder());
		final List<ProductDescription> products = productManager.searchEntities(context, orders, range);

		model.addAttribute("category", category);
		model.addAttribute("products", products);
	}

	@Autowired
	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}
}