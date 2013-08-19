package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.services.price.ExchangeManager;
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
public class PriceConverterController {
	private ExchangeManager exchangeManager;

	public PriceConverterController() {
	}

	@RequestMapping(value = "update")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String viewOrders(@RequestParam("rate") double rate) {
		exchangeManager.setExchangeRate(rate);
		return "redirect:/maintain/main";
	}

	@Autowired
	public void setExchangeManager(ExchangeManager exchangeManager) {
		this.exchangeManager = exchangeManager;
	}
}
