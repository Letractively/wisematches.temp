package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.warehouse.Attribute;
import billiongoods.server.warehouse.AttributeType;
import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/attribute")
public class AttributeMaintainController extends AbstractController {
	public AttributeMaintainController() {
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String viewAttribute(Model model, @ModelAttribute("form") Attribute.Editor form) {
		Attribute attribute = null;
		if (form.getId() != null) {
			attribute = attributeManager.getAttribute(form.getId());
		}

		if (attribute != null) {
			form.init(attribute);
		} else {
			form.setAttributeType(AttributeType.UNKNOWN);
		}
		model.addAttribute("attribute", attribute);
		model.addAttribute("attributes", attributeManager.getAttributes());
		return "/content/maintain/attribute";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String updateAttribute(@Valid @ModelAttribute("form") Attribute.Editor form, Model model, BindingResult result) {
		try {
			Attribute attribute;
			if (form.getId() == null) {
				attribute = attributeManager.createAttribute(form);
			} else {
				attribute = attributeManager.updateAttribute(form);
			}
			form.init(attribute);
			return viewAttribute(model, form);
		} catch (Exception ex) {
			result.reject("internal.error", ex.getMessage());
			return viewAttribute(model, new Attribute.Editor());
		}
	}
}
