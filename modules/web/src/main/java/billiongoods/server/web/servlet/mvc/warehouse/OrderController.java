package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.paypal.PayPalTransaction;
import billiongoods.server.services.paypal.PayPalTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/order")
public class OrderController {
	private OrderManager orderManager;
	private PayPalTransactionManager transactionManager;

	public OrderController() {
	}

	@RequestMapping("/accepted")
	public String orderAccepted(@RequestParam("o") Long orderId, Model model) {
		final Order order = orderManager.getOrder(orderId);
		model.addAttribute("order", order);
		return "/content/warehouse/order/accepted";
	}

	@RequestMapping("/rejected")
	public String orderRejected(@RequestParam("o") Long orderId, Model model) {
		final Order order = orderManager.getOrder(orderId);
		model.addAttribute("order", order);
		return "/content/warehouse/order/rejceted";
	}

	@RequestMapping("/failed")
	public String orderFailed(@RequestParam("t") Long transactionId, Model model) {
		final PayPalTransaction transaction = transactionManager.getTransaction(transactionId);
		model.addAttribute("transaction", transaction);
		return "/content/warehouse/order/failed";
	}

	@Autowired
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	@Autowired
	public void setTransactionManager(PayPalTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
