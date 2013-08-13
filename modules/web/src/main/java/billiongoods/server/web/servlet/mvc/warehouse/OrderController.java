package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/order")
public class OrderController {
	private OrderManager orderManager;

	public OrderController() {
	}

	@RequestMapping("/status")
	public String internal(@ModelAttribute("orderId") Long orderId,
						   @ModelAttribute("orderToken") String orderToken, Model model) {
		if (orderId != null && orderToken != null) {
			final Order order = orderManager.getOrder(orderId);
			if (!order.getToken().equals(orderToken)) {
				return "/content/warehouse/order/expired";
			}
			model.addAttribute("order", order);
			return "/content/warehouse/order/status";
		}
		return "/content/warehouse/order/failed";
	}

	@Autowired
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
}
