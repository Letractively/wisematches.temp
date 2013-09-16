package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.StoreAttribute;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.CategoryForm;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/category")
public class CategoryMaintainController extends AbstractController {
	public CategoryMaintainController() {
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String viewCategory(Model model, @ModelAttribute("form") CategoryForm form) {
		Category category = null;
		if (form.getId() != null) {
			category = categoryManager.getCategory(form.getId());
		}

		if (category != null) {
			form.setName(category.getName());
			form.setDescription(category.getDescription());
			form.setPosition(category.getPosition());

			if (category.getParent() != null) {
				form.setParent(category.getParent().getId());
			} else {
				form.setParent(null);
			}

			final List<Integer> attrIds = new ArrayList<>();
			final Set<StoreAttribute> attrs = category.getAttributes();
			for (StoreAttribute attribute : attrs) {
				attrIds.add(attribute.getId());
			}
			form.setAttributes(attrIds);
			model.addAttribute("category", category);
		}

		return prepareViewResult(model, form);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String updateCategory(Model model, @Valid @ModelAttribute("form") CategoryForm form, Errors errors) {
		try {
			final Set<StoreAttribute> attrs = new HashSet<>();
			final List<Integer> attributes = form.getAttributes();
			if (attributes != null) {
				for (Integer attribute : attributes) {
					attrs.add(attributeManager.getAttribute(attribute));
				}
			}

			Category parent = null;
			if (form.getParent() != null) {
				parent = categoryManager.getCategory(form.getParent());
			}

			final Category category;
			if (form.getId() == null) {
				category = categoryManager.createCategory(form.getName(), form.getDescription(), attrs, parent, form.getPosition());
			} else {
				category = categoryManager.updateCategory(form.getId(), form.getName(), form.getDescription(), attrs, parent, form.getPosition());
			}
			return "redirect:/maintain/category?id=" + category.getId();
		} catch (Exception ex) {
			errors.reject("internal.error", ex.getMessage());
		}

		return prepareViewResult(model, form);
	}

	private String prepareViewResult(Model model, CategoryForm form) {
		final Collection<StoreAttribute> attributes = new ArrayList<>(attributeManager.getAttributes());

		Category parent = categoryManager.getCategory(form.getParent());
		while (parent != null) {
			for (StoreAttribute attribute : parent.getAttributes()) {
				attributes.remove(attribute);
			}
			parent = parent.getParent();
		}

		model.addAttribute("attributes", attributes);
		return "/content/maintain/category";
	}
}
