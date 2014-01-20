package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.Member;
import billiongoods.server.services.coupon.CouponManager;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.payment.OrderState;
import billiongoods.server.services.paypal.PayPalException;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.ExpiredParametersException;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderCheckoutForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderErrorForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderTrackingForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderViewForm;
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

import java.util.Date;
import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/order")
public class OrderController extends AbstractController {
	private OrderManager orderManager;
	private CouponManager couponManager;

	private static final String ORDER_ID_PARAM = "ORDER_ID";
	public static final String ORDER_CHECKOUT_FORM_NAME = OrderCheckoutForm.class.getName();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.order.OrderController");

	public OrderController() {
		super(true, false);
	}

	@RequestMapping("")
	@Transactional(propagation = Propagation.SUPPORTS)
	public String viewOrderStatus() {
		return "redirect:/warehouse/order/status";
	}

	@RequestMapping("/checkout")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String checkoutOrder(WebRequest request) {
		final OrderCheckoutForm form = (OrderCheckoutForm) request.getAttribute(ORDER_CHECKOUT_FORM_NAME, RequestAttributes.SCOPE_REQUEST);
		final Order order = orderManager.create(getPersonality(), form.getBasket(), form.getAddress(), form.getShipmentType(), form.isEnabledTracking());
		return PayPalController.forwardCheckout(request, order);
	}

	@RequestMapping("/accepted")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String orderAccepted(WebRequest request) {
		final Long orderId = (Long) request.getAttribute(ORDER_ID_PARAM, RequestAttributes.SCOPE_REQUEST);

		final Order order = orderManager.getOrder(orderId);
		try {
			basketManager.closeBasket(getPersonality());
		} catch (Exception ex) {
			log.error("Basket can't be closed for order " + order.getId(), ex);
		}

		try {
			couponManager.redeemCoupon(order.getCoupon());
		} catch (Exception ex) {
			log.error("Coupon can't be redeemed for order " + order.getId(), ex);
		}

		request.setAttribute(ORDER_ID_PARAM, order.getId(), RequestAttributes.SCOPE_SESSION);
		return "redirect:/warehouse/order/confirm";
	}

	@RequestMapping("/rejected")
	public String orderRejected() {
		return "redirect:/warehouse/basket";
	}

	@RequestMapping("/failed")
	@Transactional(propagation = Propagation.SUPPORTS)
	public String internal(@ModelAttribute("form") OrderErrorForm form) throws PayPalException, ExpiredParametersException {
		final PayPalException exception = form.getException();
		if (exception != null) {
			throw exception;
		}
		throw new ExpiredParametersException();
	}

	@RequestMapping("/confirm")
	@Transactional(propagation = Propagation.SUPPORTS)
	public String confirmOrderStatus(Model model, WebRequest request) {
		final Long orderId = (Long) request.getAttribute(ORDER_ID_PARAM, RequestAttributes.SCOPE_SESSION);
		if (orderId != null) {
			request.removeAttribute(ORDER_ID_PARAM, RequestAttributes.SCOPE_SESSION);
			return viewOrder(orderId, orderManager.getOrder(orderId), true, model);
		}
		return "redirect:/warehouse/order/status";
	}

	@RequestMapping("/status")
	@Transactional(propagation = Propagation.SUPPORTS)
	public String viewOrderStatus(@ModelAttribute("form") OrderViewForm form, Errors errors, Model model) {
		if (form.isEmpty()) {
			return "/content/warehouse/order/track";
		}
		return processOrderStatus(form, errors, model);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@RequestMapping(value = "/status", method = RequestMethod.POST)
	public String processOrderStatus(@ModelAttribute("form") OrderViewForm form, Errors errors, Model model) {
		if (form.getOrder() == null) {
			errors.rejectValue("order", "order.error.id.empty");
		}
		if (form.getEmail() == null || form.getEmail().isEmpty()) {
			errors.rejectValue("email", "order.error.email.empty");
		}

		if (!errors.hasErrors()) {
			final Order order = orderManager.getOrder(form.getOrder());
			if (order == null || order.getPayer() == null || !order.getPayer().equalsIgnoreCase(form.getEmail().trim())) {
				errors.reject("order.error.invalid");
			} else {
				return viewOrder(form.getOrder(), order, false, model);
			}
		}
		return "/content/warehouse/order/track";
	}

	@RequestMapping("/tracking.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse changeTrackingState(@RequestBody OrderTrackingForm form, Locale locale) {
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

	@RequestMapping("/close.ajax")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ServiceResponse confirmReceivedAjax(@RequestBody OrderViewForm form, Locale locale) {
		if (form.getOrder() == null) {
			return responseFactory.failure("order.error.id.empty", locale);
		}

		final Order order = orderManager.getOrder(form.getOrder());
		if (order == null) {
			return responseFactory.failure("order.error.invalid", locale);
		}
		if (order.getOrderState() != OrderState.SHIPPED) {
			return responseFactory.failure("order.error.closed", locale);
		}

		final Member member = getMember();
		if (form.getEmail() != null && !form.getEmail().isEmpty()) { // tracking form only
			if (!order.getPayer().equalsIgnoreCase(form.getEmail())) {
				return responseFactory.failure("order.error.access", locale);
			}
		} else if (member == null || !member.idem(order.getPersonId())) { // another owner?
			return responseFactory.failure("order.error.access", locale);
		}
		orderManager.close(order.getId(), new Date(), null);
		return responseFactory.success();
	}

	private String viewOrder(Long orderId, Order order, boolean confirmation, Model model) {
		if (order == null) {
			throw new UnknownEntityException(orderId, "order");
		}
		model.addAttribute("order", order);
		model.addAttribute("confirmation", confirmation);
		return "/content/warehouse/order/view";
	}

	@Autowired
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	@Autowired
	public void setCouponManager(CouponManager couponManager) {
		this.couponManager = couponManager;
	}
}
