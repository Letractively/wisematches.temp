package billiongoods.server.web.servlet.mvc.maintain;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */

import billiongoods.server.services.price.PriceValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/maintain/service")
public class ServiceController {
	private PriceValidator priceValidator;

	public ServiceController() {
	}

	@RequestMapping("validatePrices")
	public String validatePrices(Model model) {
		model.addAttribute("active", priceValidator.isInProgress());
		model.addAttribute("summary", priceValidator.getValidationSummary());
		return "/content/maintain/priceValidation";
	}

	@RequestMapping(value = "validatePrices", method = RequestMethod.POST)
	public String validatePricesAction(@RequestParam("action") String action, Model model) {
		if ("start".equalsIgnoreCase(action)) {
			if (!priceValidator.isInProgress()) {
				priceValidator.startPriceValidation();
			}
		} else if ("stop".equalsIgnoreCase(action)) {
			if (priceValidator.isInProgress()) {
				priceValidator.stopPriceValidation();
			}
		}
		return "redirect:/maintain/service/validatePrices";
	}

	@Autowired
	public void setPriceValidator(PriceValidator priceValidator) {
		this.priceValidator = priceValidator;
	}
}
