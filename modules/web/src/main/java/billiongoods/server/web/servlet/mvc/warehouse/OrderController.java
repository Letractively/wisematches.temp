package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.Visitor;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.paypal.PayPalException;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.ExpiredParametersException;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderCheckoutForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderErrorForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderViewForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.TrackingForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/order")
public class OrderController extends AbstractController {
	private OrderManager orderManager;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.order.OrderController");

	public OrderController() {
	}

	@RequestMapping("/checkout")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String checkoutOrder(WebRequest request) {
		final Basket basket = (Basket) request.getAttribute("basket", RequestAttributes.SCOPE_REQUEST);
		final OrderCheckoutForm form = (OrderCheckoutForm) request.getAttribute("form", RequestAttributes.SCOPE_REQUEST);

		final Order order = orderManager.create(getPrincipal(), basket, form, form.getShipment(), form.isNotifications());
		request.setAttribute("order", order, RequestAttributes.SCOPE_REQUEST);
		return "forward:/warehouse/paypal/checkout";
	}

	@RequestMapping("/accepted")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String orderAccepted(WebRequest request) {
		final Long orderId = (Long) request.getAttribute("OrderId", RequestAttributes.SCOPE_REQUEST);

		final Order order = orderManager.getOrder(orderId);
		try {
			basketManager.closeBasket(new Visitor(order.getBuyer()));
		} catch (Exception ex) {
			log.error("Basket can't be closed", ex);
		}

		request.setAttribute("order", order, RequestAttributes.SCOPE_SESSION);
		return "redirect:/warehouse/order/status";
	}

	@RequestMapping("/rejected")
	public String orderRejected() {
		return "redirect:/warehouse/basket";
	}

	@RequestMapping("/failed")
	@Transactional(propagation = Propagation.SUPPORTS)
	public String internal(@ModelAttribute("form") OrderErrorForm form) throws PayPalException, ExpiredParametersException {
		if (form.getException() == null) {
			throw new ExpiredParametersException();
		}
		throw form.getException();
	}

	@RequestMapping("/status")
	@Transactional(propagation = Propagation.SUPPORTS)
	public String viewOrderStatus(@ModelAttribute("form") OrderViewForm form, Errors errors, Model model, WebRequest request) {
		final Order order = (Order) request.getAttribute("order", RequestAttributes.SCOPE_SESSION);
		if (order != null) {
			request.removeAttribute("order", RequestAttributes.SCOPE_SESSION);
			return viewOrder(model, order, true);
		}

		hideNavigation(model);

		if (form.isEmpty()) {
			return "/content/warehouse/order/track";
		}
		return processOrderStatus(form, errors, model);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
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
				return viewOrder(model, order, false);
			}
		}
		return "/content/warehouse/order/track";
	}

	@RequestMapping("/tracking.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse changeTrackingState(@RequestBody TrackingForm form, Locale locale) {
		if (form.getOrder() == null) {
			return responseFactory.failure("order.error.id.empty", locale);
		}
		if (form.getEmail() == null || form.getEmail().isEmpty()) {
			return responseFactory.failure("order.error.email.empty", locale);
		}

		final Order order = orderManager.getOrder(form.getOrder());
		if (order == null || !order.getPayer().equalsIgnoreCase(form.getEmail())) {
			return responseFactory.failure("order.error.invalid", locale);
		} else {
			orderManager.setOrderTracking(order, form.isEnable());
			return responseFactory.success();
		}
	}

	private String viewOrder(Model model, Order order, boolean isNew) {
		model.addAttribute("order", order);
		model.addAttribute("orderIsNew", isNew);
		return "/content/warehouse/order/view";
	}

	@Autowired
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}
}
