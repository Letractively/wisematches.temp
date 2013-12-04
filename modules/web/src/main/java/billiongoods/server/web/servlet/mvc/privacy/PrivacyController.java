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
	public String privacy(Model model) {
		return "redirect:/privacy/view";
	}


	@RequestMapping("/view")
	public String privacyView(Model model) {
		final OrdersSummary summary = orderManager.getOrdersSummary(getPrincipal());
		model.addAttribute("ordersSummary", summary);

		return "/content/privacy/view";
	}

	@RequestMapping("/addresses")
	public String privacyAddresses(Model model) {
		return "/content/privacy/addresses";
	}

	@RequestMapping("/wishlist")
	public String privacyWishList(Model model) {
		return "/content/privacy/wishlist";
	}

	@RequestMapping("/subscriptions")
	public String privacySubscriptions(Model model) {
		return "/content/privacy/subscriptions";
	}

	@RequestMapping("/coupons")
	public String privacyCoupons(Model model) {
		return "/content/privacy/coupons";
	}

	@RequestMapping("/social")
	public String privacySocial(Model model) {
		return "/content/privacy/social";
	}

	@Autowired
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
}
