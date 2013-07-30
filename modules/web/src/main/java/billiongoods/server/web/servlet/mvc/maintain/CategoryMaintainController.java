package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.warehouse.Attribute;
import billiongoods.server.warehouse.AttributeManager;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.CategoryManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.CategoryForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/category")
public class CategoryMaintainController extends AbstractController {
	private CategoryManager categoryManager;
	private AttributeManager attributeManager;

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
			if (category.getParent() != null) {
				form.setParent(category.getParent().getId());
			} else {
				form.setParent(null);
			}

			int index = 0;
			final Set<Attribute> attributes = category.getAttributes();
			final Integer[] attrs = new Integer[attributes.size()];
			for (Attribute attribute : attributes) {
				attrs[index++] = attribute.getId();
			}
			form.setAttributes(attrs);
		}

		model.addAttribute("category", category);

		model.addAttribute("catalog", categoryManager.getCatalog());
		model.addAttribute("attributes", attributeManager.getAttributes());
		return "/content/maintain/category";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String updateCategory(Model model, @Valid @ModelAttribute("form") CategoryForm form) {
		final Set<Attribute> attrs = new HashSet<>();
		final Integer[] attributes = form.getAttributes();
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
	}

	@Autowired
	public void setCategoryManager(CategoryManager categoryManager) {
		this.categoryManager = categoryManager;
	}

	@Autowired
	public void setAttributeManager(AttributeManager attributeManager) {
		this.attributeManager = attributeManager;
	}
}