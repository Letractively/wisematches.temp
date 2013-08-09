package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.Personality;
import billiongoods.server.services.ServerDescriptor;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.basket.BasketManager;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.payment.PaymentSystem;
import billiongoods.server.services.paypal.CheckoutToken;
import billiongoods.server.services.paypal.PayPalException;
import billiongoods.server.services.paypal.PayPalExpressCheckout;
import billiongoods.server.services.paypal.WebAddressResolver;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderConfirmForm;
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

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;

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

	private final WebAddressResolver addressResolver = new TheWebAddressResolver();

	private static final String RUSSIAN_FEDERATION = "Russian Federation";

	private static final Logger log = LoggerFactory.getLogger("billiongoods.order.PayPalController");

	public PayPalController() {
	}

	@RequestMapping("")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String placeOrder(@ModelAttribute("order") OrderConfirmForm form, Errors errors, Model model) {
		final Personality principal = getPrincipal();

		final Basket basket = basketManager.getBasket(principal);

		final Integer[] itemNumbers = form.getItemNumbers();
		final int[] itemQuantities = form.getItemQuantities();

		for (BasketItem item : new ArrayList<>(basket.getBasketItems())) {
			final int number = item.getNumber();

			int index = Arrays.binarySearch(itemNumbers, number);
			if (index < 0) {
				basketManager.removeBasketItem(principal, number);
			}

			if (item.getQuantity() != itemQuantities[index]) {
				basketManager.updateBasketItem(principal, number, itemQuantities[index]);
			}
		}

		final Order order = orderManager.createOrder(getPrincipal(), basket, form, PaymentSystem.PAY_PAL);

		log.info("Order for personal " + getPrincipal() + " has been created.");

		try {
			final String endpoint = expressCheckout.getMode().getPayPalEndpoint();

			final CheckoutToken token = expressCheckout.createExpressCheckout(order, addressResolver);
			log.info("PayPal token has been generated: " + token);

			return "redirect:" + endpoint + "?_express-checkout&token=" + token.getToken();
		} catch (PayPalException ex) {
			orderManager.deleteOrder(order.getId());

			return "redirect:http://paypal.ru/asdadfsaf.sdsa/fasdfs?cmd";
		}
	}

	@RequestMapping("/accepted")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String orderAccepted(Model model, HttpServletRequest request) {
		return "/content/warehouse/order/accepted";
	}

	@RequestMapping("/rejected")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String orderRejected(Model model, HttpServletRequest request) {
		return "/content/warehouse/order/rejected";
	}

	/**
	 * https://developer.paypal.com/webapps/developer/docs/classic/ipn/integration-guide/IPNIntro/
	 */
	@RequestMapping("/ipn")
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public String orderPayPalIPN(Model model, HttpServletRequest request) {
		return "/";
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

	private class TheWebAddressResolver implements WebAddressResolver {
		private TheWebAddressResolver() {
		}

		@Override
		public String getReturnURL() {
			return serverDescriptor.getWebHostName() + "/warehouse/order/accepted";
		}

		@Override
		public String getCancelURL() {
			return serverDescriptor.getWebHostName() + "/warehouse/order/rejected";
		}

		@Override
		public String getOrderURL(Order order) {
			return serverDescriptor.getWebHostName() + "/warehouse/order/view";
		}

		@Override
		public String getArticleURL(Integer number) {
			return serverDescriptor.getWebHostName() + "/warehouse/article/" + number;
		}
	}
}
