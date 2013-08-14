package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.services.ServerDescriptor;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.paypal.PayPalException;
import billiongoods.server.services.paypal.PayPalExpressCheckout;
import billiongoods.server.services.paypal.PayPalTransaction;
import billiongoods.server.services.paypal.TransactionResolution;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderErrorForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderViewForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/paypal")
public class PayPalController extends AbstractController {
	private OrderManager orderManager;
	private ServerDescriptor serverDescriptor;
	private PayPalExpressCheckout expressCheckout;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.order.PayPalController");

	public PayPalController() {
	}

	@RequestMapping("/checkout")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String checkoutOrder(@RequestParam("order") Long orderId) {
		try {
			final Order order = orderManager.getOrder(orderId);

			final String orderUrl = serverDescriptor.getWebHostName() + "/warehouse/order/view";
			final String returnUrl = serverDescriptor.getWebHostName() + "/warehouse/paypal/accepted";
			final String cancelUrl = serverDescriptor.getWebHostName() + "/warehouse/paypal/rejected";

			final PayPalTransaction transaction = expressCheckout.initiateExpressCheckout(order, orderUrl, returnUrl, cancelUrl);
			log.info("PayPal token has been generated: " + transaction.getToken());

			orderManager.bill(order.getId(), transaction.getToken());
			return "redirect:" + expressCheckout.getExpressCheckoutEndPoint(transaction.getToken());

		} catch (PayPalException ex) {
			orderManager.failed(orderId, ex.getMessage());

			log.error("PayPal processing error: " + ex.getMessage(), ex);
			return "forward:/warehouse/basket/rollback";
		}
	}

	@RequestMapping("/accepted")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String orderAccepted(@RequestParam("token") String token, RedirectAttributes attributes) {
		return processOrderState(token, attributes, true);
	}

	@RequestMapping("/rejected")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String orderRejected(@RequestParam("token") String token, RedirectAttributes attributes) {
		return processOrderState(token, attributes, false);
	}

	private String processOrderState(String token, RedirectAttributes attributes, boolean accepted) {
		try {
			final PayPalTransaction transaction = expressCheckout.finalizeExpressCheckout(token, accepted);
			if (transaction.getResolution() == TransactionResolution.APPROVED) {
				orderManager.accept(transaction.getOrderId(), transaction.getPayer());
			} else if (transaction.getResolution() == TransactionResolution.REJECTED) {
				orderManager.reject(transaction.getOrderId(), transaction.getPayer());
			} else {
				throw new UnknownEntityException(token, "transaction");
			}
			attributes.addFlashAttribute("form", new OrderViewForm(transaction.getOrderId(), transaction.getPayer()));
			return "redirect:/warehouse/order/status";
		} catch (PayPalException ex) {
			log.error("Payment can't be processed: " + token, ex);
			orderManager.failed(token, ex.getMessage());

			attributes.addFlashAttribute("form", new OrderErrorForm(token, ex));
			return "redirect:/warehouse/order/failed";
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

		try {
			expressCheckout.registerIPNMessage(parameterMap);
		} catch (PayPalException e) {
			log.error("PayPal IPN message can't be processed", e);
		}

		return responseFactory.success();
	}

	@Autowired
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
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
