package billiongoods.server.web.servlet.mvc.privacy;

import billiongoods.server.services.payment.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.PageableForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/privacy")
public class PrivacyController extends AbstractController {
	private OrderManager orderManager;

	public PrivacyController() {
	}

	@RequestMapping("/view")
	public String privacyView(Model model) {
		final OrdersSummary summary = orderManager.getOrdersSummary(getPrincipal());
		model.addAttribute("ordersSummary", summary);

		return "/content/privacy/view";
	}

	@RequestMapping(value = {"/orders/{state}"})
	public String privacyOrders(@PathVariable("state") String orderState,
								@ModelAttribute("pageableForm") PageableForm pageableForm, Model model) {
		final OrderState state = OrderState.valueOf(orderState.toUpperCase());
		final OrderContext context = new OrderContext(getPrincipal(), state);

		final int totalCount = orderManager.getTotalCount(context);
		pageableForm.initialize(totalCount, totalCount);

		final List<Order> orders = orderManager.searchEntities(context, null, pageableForm.getRange(), pageableForm.getOrders());
		model.addAttribute("orders", orders);
		return "/content/privacy/orders";
	}

	@RequestMapping("/addresses")
	public String privacyAddresses(Model model) {
		return "/content/privacy/addresses";
	}

	@RequestMapping("/wishlist")
	public String privacyWishList(Model model) {
		return "/content/privacy/wishlist";
	}

	@RequestMapping("/settings")
	public String privacySettings(Model model) {
		return "/content/privacy/settings";
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
