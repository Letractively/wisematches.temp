package billiongoods.server.web.servlet.mvc.maintain;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */

import billiongoods.server.warehouse.impl.BanggoodPriceValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/maintain/service")
public class ServiceController {
	private BanggoodPriceValidator priceValidator;

	public ServiceController() {
	}

	@RequestMapping("validatePrices")
	public String validatePrices(Model model) {
		model.addAttribute("active", priceValidator.isActive());
		return "/content/maintain/priceValidation";
	}

	@RequestMapping(value = "validatePrices", method = RequestMethod.POST)
	public String validatePricesAction(Model model) {
		if (!priceValidator.isActive()) {
			priceValidator.validatePrice();
		}
		return "redirect:/maintain/service/validatePrices";
	}

	@Autowired
	public void setPriceValidator(BanggoodPriceValidator priceValidator) {
		this.priceValidator = priceValidator;
	}
}
