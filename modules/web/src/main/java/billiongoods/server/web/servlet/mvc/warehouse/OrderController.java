package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.paypal.PayPalException;
import billiongoods.server.warehouse.ArticleManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.ExpiredParametersException;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderErrorForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderViewForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/order")
public class OrderController extends AbstractController {
	private OrderManager orderManager;
	private ArticleManager articleManager;

	public OrderController() {
	}

	@RequestMapping("/status")
	public String viewOrderStatus(@ModelAttribute("form") OrderViewForm form, Errors errors, Model model) {
		hideNavigation(model);

		if (form.isEmpty()) {
			return "/content/warehouse/order/track";
		}
		return processOrderStatus(form, errors, model);
	}

	@RequestMapping(value = "/status", method = RequestMethod.POST)
	public String processOrderStatus(@ModelAttribute("form") OrderViewForm form, Errors errors, Model model) {
		hideNavigation(model);

		if (form.getOrder() == null) {
			errors.rejectValue("order", "order.error.id.empty");
		}
		if (form.getEmail() == null || form.getEmail().isEmpty()) {
			errors.rejectValue("email", "order.error.email.empty");
		}

		if (!errors.hasErrors()) {
			final Order order = orderManager.getOrder(form.getOrder());
			if (order == null || !order.getPayer().equalsIgnoreCase(form.getEmail())) {
				errors.reject("order.error.invalid");
			} else {
				model.addAttribute("order", order);
				model.addAttribute("articleManager", articleManager);
				return "/content/warehouse/order/view";
			}
		}
		return "/content/warehouse/order/track";
	}

	@RequestMapping("/failed")
	public String internal(@ModelAttribute("form") OrderErrorForm form) throws PayPalException, ExpiredParametersException {
		if (form.getException() == null) {
			throw new ExpiredParametersException();
		}
		throw form.getException();
	}

	@Autowired
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	@Autowired
	public void setArticleManager(ArticleManager articleManager) {
		this.articleManager = articleManager;
	}
}
