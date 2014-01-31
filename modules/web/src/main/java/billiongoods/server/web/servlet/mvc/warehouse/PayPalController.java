package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.services.ServerDescriptor;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.paypal.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;
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

	private static final String ORDER_ID_PARAM = "ORDER_ID";

	private static final Logger log = LoggerFactory.getLogger("billiongoods.order.PayPalController");

	public PayPalController() {
	}

	@RequestMapping("/checkout")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String checkoutOrder(WebRequest request, HttpSession session) {
		final Long orderId = (Long) request.getAttribute(ORDER_ID_PARAM, RequestAttributes.SCOPE_REQUEST);
		try {
			final Order order = orderManager.getOrder(orderId);

			final String orderUrl = serverDescriptor.getWebHostName() + "/warehouse/order/view";
			final String returnUrl = serverDescriptor.getWebHostName() + "/warehouse/paypal/accepted?JSESSIONID=" + session.getId();
			final String cancelUrl = serverDescriptor.getWebHostName() + "/warehouse/paypal/rejected?JSESSIONID=" + session.getId();

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
	public String orderAccepted(@RequestParam("token") String token, WebRequest request) {
		return processOrderState(token, true, request);
	}

	@RequestMapping("/rejected")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String orderRejected(@RequestParam("token") String token, WebRequest request) {
		return processOrderState(token, false, request);
	}

	private String processOrderState(String token, boolean accepted, WebRequest request) {
		try {
			final PayPalTransaction transaction = expressCheckout.finalizeExpressCheckout(token, accepted);
			request.setAttribute(ORDER_ID_PARAM, transaction.getOrderId(), RequestAttributes.SCOPE_REQUEST);
			if (transaction.getResolution() == TransactionResolution.APPROVED) {
				if (transaction.getTransactionId() == null) {
					throw new PayPalSystemException(token, "There is no transactionID: payment is not approved by PayPal");
				}

				orderManager.accept(transaction.getOrderId(), transaction.getPayer(), transaction.getPayerName(), transaction.getPayerNote(), transaction.getTransactionId());
				return "forward:/warehouse/order/accepted";
			} else if (transaction.getResolution() == TransactionResolution.REJECTED) {
				orderManager.reject(transaction.getOrderId(), transaction.getPayer(), transaction.getTransactionId(), transaction.getPayerNote());
				return "forward:/warehouse/order/rejected";
			}
			throw new UnknownEntityException(token, "transaction");
		} catch (PayPalException ex) {
			log.error("Payment can't be processed: " + token, ex);
			orderManager.failed(token, ex.getMessage());
			return "forward:/warehouse/order/failed";
		}
	}

	/**
	 * https://developer.paypal.com/webapps/developer/docs/classic/ipn/integration-guide/IPNIntro/
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/ipn", method = RequestMethod.POST)
	public ServiceResponse orderPayPalIPN(Model model, WebRequest request) {
		final Map<String, String[]> parameterMap = request.getParameterMap();
		log.info("IPN message received: " + parameterMap);

		try {
			expressCheckout.registerIPNMessage(parameterMap);
		} catch (PayPalException e) {
			log.error("PayPal IPN message can't be processed", e);
		}

		return responseFactory.success();
	}

	public static String forwardCheckout(WebRequest request, Order order) {
		request.setAttribute(ORDER_ID_PARAM, order.getId(), RequestAttributes.SCOPE_REQUEST);
		return "forward:/warehouse/paypal/checkout";
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
