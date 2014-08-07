package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.Member;
import billiongoods.server.services.coupon.CouponManager;
import billiongoods.server.services.payment.*;
import billiongoods.server.services.paypal.PayPalException;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderCheckoutForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderViewForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.ParcelViewForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/order")
public class OrderController extends AbstractController {
	private OrderManager orderManager;
	private CouponManager couponManager;

	private static final String ORDER_ID_PARAM = "ORDER_ID_PARAM";
	private static final String ORDER_EXCEPTION_PARAM = "ORDER_EXCEPTION_PARAM";

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
		final Order order = orderManager.create(getPersonality(), form.getBasket(), form.getAddress(), form.getShipmentType());
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
			final OrderDiscount discount = order.getDiscount();
			couponManager.redeemCoupon(discount.getCoupon(), discount.getAmount());
		} catch (Exception ex) {
			log.error("Coupon can't be redeemed for order " + order.getId(), ex);
		}

		request.setAttribute(ORDER_ID_PARAM, order.getId(), RequestAttributes.SCOPE_SESSION);
		return "redirect:/warehouse/order/confirm";
	}

	@RequestMapping({"/rejected", "/failed"})
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String orderRejected(WebRequest request, Locale locale) {
		final Long orderId = (Long) request.getAttribute(ORDER_ID_PARAM, RequestAttributes.SCOPE_REQUEST);
		final PayPalException exception = (PayPalException) request.getAttribute(ORDER_EXCEPTION_PARAM, RequestAttributes.SCOPE_REQUEST);

		orderManager.remove(orderId);

		log.info("The order {} has been rejected or removed: {}", orderId, exception);

		if (exception != null && messageSource.getDefaultMessage("paypal.error." + exception.getCode(), null, locale) == null) {
			log.error("Untranslated PayPal error code: {} - > {}", exception.getCode(), exception.getMessage());
		}
		return BasketController.rollbackBasket(exception, request);
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
			if (!errors.hasFieldErrors("order")) {
				errors.rejectValue("order", "order.error.id.empty");
			}
		}
		if (form.getEmail() == null || form.getEmail().isEmpty()) {
			errors.rejectValue("email", "order.error.email.empty");
		}

		if (!errors.hasErrors()) {
			final Order order = orderManager.getOrder(form.getOrder());
			if (order == null) {
				errors.reject("order.error.invalid");
			}

			if (order != null && !errors.hasErrors()) {
				final OrderPayment payment = order.getPayment();
				if (payment.getPayer() == null || !payment.getPayer().equalsIgnoreCase(form.getEmail().trim())) {
					errors.reject("order.error.invalid");
				} else {
					return viewOrder(form.getOrder(), order, false, model);
				}
			}
		}
		return "/content/warehouse/order/track";
	}

	@RequestMapping("/close")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String confirmReceivedAjax(@ModelAttribute("form") ParcelViewForm form, Model model, Errors errors) {
		if (form.getOrder() == null) {
			errors.reject("order.error.id.empty");
		}

		final Order order = orderManager.getOrder(form.getOrder());
		if (order == null) {
			errors.reject("order.error.invalid");
		}

		if (order != null) {
			final Parcel parcel = order.getParcel(form.getParcel());
			if (parcel == null) {
				errors.reject("order.error.invalid");
			}

			if (parcel != null) {
				if (parcel.getState() != ParcelState.SHIPPED) {
					errors.reject("order.error.closed");
				}

				final Member member = getMember();
				if (form.getEmail() != null && !form.getEmail().isEmpty()) { // tracking form only
					if (!order.getPayment().getPayer().equalsIgnoreCase(form.getEmail())) {
						errors.reject("order.error.access");
					}
				} else if (member == null || !member.idem(order.getPersonId())) { // another owner?
					errors.reject("order.error.access");
				}

				if (!errors.hasErrors()) {
					orderManager.close(order.getId(), form.getParcel(), LocalDateTime.now(), null);
				}
			}
		}
		return viewOrder(form.getOrder(), order, false, model);
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


	public static String forwardAccepted(Long orderId, WebRequest request) {
		request.setAttribute(ORDER_ID_PARAM, orderId, RequestAttributes.SCOPE_REQUEST);
		return "forward:/warehouse/order/accepted";
	}

	public static String forwardRejected(Long orderId, WebRequest request) {
		request.setAttribute(ORDER_ID_PARAM, orderId, RequestAttributes.SCOPE_REQUEST);
		return "forward:/warehouse/order/rejected";
	}

	public static String forwardFailed(Long orderId, PayPalException exception, WebRequest request) {
		request.setAttribute(ORDER_ID_PARAM, orderId, RequestAttributes.SCOPE_REQUEST);
		request.setAttribute(ORDER_EXCEPTION_PARAM, exception, RequestAttributes.SCOPE_REQUEST);
		return "forward:/warehouse/order/failed";
	}
}
