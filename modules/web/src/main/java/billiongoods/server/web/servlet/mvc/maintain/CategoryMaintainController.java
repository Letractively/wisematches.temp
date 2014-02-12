package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.warehouse.Attribute;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Parameter;
import billiongoods.server.warehouse.RelationshipManager;
import billiongoods.server.web.services.ProductSymbolicService;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.AttributeForm;
import billiongoods.server.web.servlet.mvc.maintain.form.CategoryForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/category")
public class CategoryMaintainController extends AbstractController {
	private RelationshipManager relationshipManager;
	private ProductSymbolicService symbolicConverter;

	public CategoryMaintainController() {
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String viewCategory(@ModelAttribute("form") CategoryForm form, Model model) {
		Category category = null;
		if (form.getId() != null) {
			category = categoryManager.getCategory(form.getId());
		}

		if (category != null) {
			form.setName(category.getName());
			if (category.getSymbolic() == null || category.getSymbolic().isEmpty()) {
				form.setSymbolic(symbolicConverter.generateSymbolic(category.getName()));
			} else {
				form.setSymbolic(category.getSymbolic());
			}
			form.setSymbolic(category.getSymbolic());
			form.setDescription(category.getDescription());
			form.setPosition(category.getPosition());

			if (category.getParent() != null) {
				form.setParent(category.getParent().getId());
			} else {
				form.setParent(null);
			}

			final Set<Integer> attrIds = new HashSet<>();
			final Collection<Parameter> parameters = category.getParameters();
			for (Parameter p : parameters) {
				attrIds.add(p.getAttribute().getId());
			}
			form.setAttributes(attrIds);

			model.addAttribute("groups", relationshipManager.searchGroups(category.getId()));
		}
		return prepareViewResult(model);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String updateCategory(@Valid @ModelAttribute("form") CategoryForm form, Errors errors, Model model) {
		try {
			Category parent = null;
			if (form.getParent() != null) {
				parent = categoryManager.getCategory(form.getParent());
			}

			final Category category;
			if (form.getId() == null) {
				category = categoryManager.createCategory(new Category.Editor(form.getName(), form.getSymbolic(), form.getDescription(), parent, form.getPosition(), form.getAttributes()));
			} else {
				category = categoryManager.updateCategory(new Category.Editor(form.getId(), form.getName(), form.getSymbolic(), form.getDescription(), parent, form.getPosition(), form.getAttributes()));
			}
			return "redirect:/maintain/category?id=" + category.getId();
		} catch (Exception ex) {
			errors.reject("internal.error", ex.getMessage());
		}

		return prepareViewResult(model);
	}

	@RequestMapping("parameterAddValue.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse addParameterValue(@RequestBody AttributeForm form, Locale locale) {
		final Category category = categoryManager.getCategory(form.getCategoryId());
		if (category == null) {
			return responseFactory.failure("category.unknown", locale);
		}

		final Attribute attribute = attributeManager.getAttribute(form.getAttributeId());
		if (attribute == null) {
			return responseFactory.failure("attribute.unknown", locale);
		}

		if (form.getValue() == null) {
			return responseFactory.failure("empty.value", locale);
		}
		categoryManager.addParameterValue(category, attribute, form.getValue());
		return responseFactory.success();
	}

	@RequestMapping(value = "/symbolic.ajax")
	public ServiceResponse generateSymbolic(@RequestParam("name") String name, Locale locale) {
		return responseFactory.success(symbolicConverter.generateSymbolic(name));
	}

	private String prepareViewResult(Model model) {
		model.addAttribute("attributes", attributeManager.getAttributes());
		return "/content/maintain/category";
	}

	@Autowired
	public void setSymbolicConverter(ProductSymbolicService symbolicConverter) {
		this.symbolicConverter = symbolicConverter;
	}

	@Autowired
	public void setRelationshipManager(RelationshipManager relationshipManager) {
		this.relationshipManager = relationshipManager;
	}
}
