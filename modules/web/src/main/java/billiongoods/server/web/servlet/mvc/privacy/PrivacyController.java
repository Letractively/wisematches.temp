package billiongoods.server.web.servlet.mvc.privacy;

import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.payment.OrdersSummary;
import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/privacy")
public class PrivacyController extends AbstractController {
	private OrderManager orderManager;

	public PrivacyController() {
	}

	@RequestMapping("")
	public String privacy() {
		return "redirect:/privacy/view";
	}

	@RequestMapping("/view")
	public String privacyView(Model model) {
		final OrdersSummary summary = orderManager.getOrdersSummary(getMember());
		model.addAttribute("ordersSummary", summary);

		return "/content/privacy/view";
	}

	@RequestMapping("/coupons")
	public String privacyCoupons(Model model) {
		return "/content/privacy/coupons";
	}

	@Autowired
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
}
