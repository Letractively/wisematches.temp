package billiongoods.server.web.servlet.mvc.maintain;

import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/maintain/cache")
public class BasketMaintainController extends AbstractController {
	public BasketMaintainController() {
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public String main() {
		return "/content/maintain/cache";
	}

	@RequestMapping(value = "/resetBasket", method = RequestMethod.GET)
	public String resetBasketCache() {
		basketManager.clearCache();

		return "/content/maintain/cache";
	}
}
