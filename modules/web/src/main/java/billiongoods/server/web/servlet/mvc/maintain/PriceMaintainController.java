package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.services.price.PriceConverter;
import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/exchange")
public class PriceMaintainController extends AbstractController {
	private PriceConverter priceConverter;

	public PriceMaintainController() {
	}

	@RequestMapping(value = "update")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String viewOrders(@RequestParam("rate") double rate) {
		priceConverter.setExchangeRate(rate);
		return "redirect:/maintain/main";
	}

	@Autowired
	public void setPriceConverter(PriceConverter priceConverter) {
		this.priceConverter = priceConverter;
	}
}
