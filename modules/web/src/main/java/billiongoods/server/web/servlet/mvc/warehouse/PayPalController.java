package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.Personality;
import billiongoods.server.services.ServerDescriptor;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketManager;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.payment.Shipment;
import billiongoods.server.services.paypal.PayPalException;
import billiongoods.server.services.paypal.PayPalExpressCheckout;
import billiongoods.server.services.paypal.PayPalTransaction;
import billiongoods.server.services.paypal.TransactionResolution;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.PaymentInfo;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/order/paypal")
public class PayPalController extends AbstractController {
	private OrderManager orderManager;
	private BasketManager basketManager;
	private ServerDescriptor serverDescriptor;
	private PayPalExpressCheckout expressCheckout;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.order.PayPalController");

	public PayPalController() {
	}

	@RequestMapping("")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String placeOrder(@ModelAttribute("order") OrderForm form, Errors errors, Model model) {
		final Personality principal = getPrincipal();
		final Basket basket = basketManager.getBasket(principal);

		final PaymentInfo info = new PaymentInfo(basket, form.getShipment());
		final Shipment shipment = new Shipment(info.getShipment(), form, form.getShipment());
		final Order order = orderManager.create(getPrincipal(), basket, shipment, form.isNotifications());

		log.info("Order for personal " + getPrincipal() + " has been created.");

		try {
			final String orderUrl = serverDescriptor.getWebHostName() + "/warehouse/order/view";
			final String returnUrl = serverDescriptor.getWebHostName() + "/warehouse/order/paypal/accepted";
			final String cancelUrl = serverDescriptor.getWebHostName() + "/warehouse/order/paypal/rejected";

			final PayPalTransaction transaction = expressCheckout.initiateExpressCheckout(order, orderUrl, returnUrl, cancelUrl);
			log.info("PayPal token has been generated: " + transaction.getToken());

			if (transaction.getResolution() == TransactionResolution.FAILED) {
				errors.reject("error.internal");
				return "forward:/warehouse/basket/rollback";
			} else {
				orderManager.bill(order.getId(), transaction.getToken());
				return "redirect:" + expressCheckout.getExpressCheckoutEndPoint(transaction.getToken());
			}
		} catch (PayPalException ex) {
			log.error("PayPal processing error: " + ex.getMessage(), ex);

			orderManager.failed(order.getId(), ex.getMessage());
			errors.reject("error.internal");
			return "forward:/warehouse/basket/rollback";
		}
	}

	@RequestMapping("/accepted")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String orderAccepted(@RequestParam("token") String token) {
		try {
			final PayPalTransaction transaction = expressCheckout.finalizeExpressCheckout(token, true);
			orderManager.accept(transaction.getOrderId(), transaction.getPayer());
			return "redirect:/warehouse/order/accepted?o=" + transaction.getOrderId();
		} catch (PayPalException ex) {
			log.error("Payment can't be processed: " + token, ex);

			if (ex.getTransaction() != null) {
				orderManager.failed(ex.getTransaction().getId(), ex.getMessage());
			}
			return "redirect:/warehouse/order/failed?t=" + token;
		}
	}

	@RequestMapping("/rejected")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String orderRejected(@RequestParam("token") String token) {
		try {
			final PayPalTransaction transaction = expressCheckout.finalizeExpressCheckout(token, false);
			return "redirect:/warehouse/order/rejected?o=" + transaction.getId();
		} catch (PayPalException ex) {
			log.error("Payment can't be processed: " + token, ex);

			if (ex.getTransaction() != null) {
				orderManager.failed(ex.getTransaction().getId(), ex.getMessage());
			}
			return "redirect:/warehouse/order/failed?t=" + token;
		}
	}

	/**
	 * https://developer.paypal.com/webapps/developer/docs/classic/ipn/integration-guide/IPNIntro/
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/ipn", method = RequestMethod.POST)
	public ServiceResponse orderPayPalIPN(Model model, HttpServletRequest request) {
		final Map<String, String[]> parameterMap = request.getParameterMap();
		log.info("IPN message received: " + parameterMap);

		expressCheckout.registerIPNMessage(parameterMap);

		return responseFactory.success();
	}

	@Autowired
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}

	@Autowired
	public void setBasketManager(BasketManager basketManager) {
		this.basketManager = basketManager;
	}

	@Autowired
	public void setServerDescriptor(ServerDescriptor serverDescriptor) {
		this.serverDescriptor = serverDescriptor;
	}

	@Autowired
	public void setExpressCheckout(PayPalExpressCheckout expressCheckout) {
		this.expressCheckout = expressCheckout;
	}
}
