package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.warehouse.Attribute;
import billiongoods.server.warehouse.Category;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.maintain.form.AttributeForm;
import billiongoods.server.web.servlet.mvc.maintain.form.CategoryForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
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
			final Set<Attribute> attrs = category.getAttributes();
			for (Attribute attribute : attrs) {
				attrIds.add(attribute.getId());
			}
			form.setAttributes(attrIds);
			model.addAttribute("category", category);
		}

		return prepareViewResult(model);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String updateCategory(Model model, @Valid @ModelAttribute("form") CategoryForm form, Errors errors) {
		try {
			final Set<Attribute> attrs = new HashSet<>();
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
				category = categoryManager.createCategory(new Category.Editor(form.getName(), form.getDescription(), parent, form.getPosition()));
			} else {
				category = categoryManager.updateCategory(new Category.Editor(form.getId(), form.getName(), form.getDescription(), parent, form.getPosition()));
			}
			return "redirect:/maintain/category?id=" + category.getId();
		} catch (Exception ex) {
			errors.reject("internal.error", ex.getMessage());
		}

		return prepareViewResult(model);
	}

	@RequestMapping("parameterAdd.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse addParameter(@RequestBody AttributeForm form, Locale locale) {
		final Category category = categoryManager.getCategory(form.getCategoryId());
		if (category == null) {
			return responseFactory.failure("category.unknown", locale);
		}

		final Attribute attribute = attributeManager.getAttribute(form.getAttributeId());
		if (attribute == null) {
			return responseFactory.failure("attribute.unknown", locale);
		}
		categoryManager.addParameter(category, attribute);
		return responseFactory.success();
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

	private String prepareViewResult(Model model) {
		model.addAttribute("attributes", attributeManager.getAttributes());
		return "/content/maintain/category";
	}
}
