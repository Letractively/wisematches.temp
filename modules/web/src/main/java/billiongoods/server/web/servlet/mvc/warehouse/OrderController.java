package billiongoods.server.web.servlet.mvc.warehouse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/order")
public class OrderController {
	public OrderController() {
	}

	@RequestMapping("")
	public String placeOrder(Model model) {
		// place order to PayPal, get response and redirect to PayPal

//		http://www.integratingstuff.com/2010/07/17/paypal-express-checkout-with-java/

		return "redirect:http://paypal.ru/asdadfsaf.sdsa/fasdfs?cmd";
	}

	@RequestMapping("/accepted")
	public String orderAccepted(Model model, HttpServletRequest request) {
		return "/content/warehouse/order/accepted";
	}

	@RequestMapping("/rejected")
	public String orderRejected(Model model, HttpServletRequest request) {
		return "/content/warehouse/order/rejected";
	}

	/**
	 * https://developer.paypal.com/webapps/developer/docs/classic/ipn/integration-guide/IPNIntro/
	 */
	@RequestMapping("/paypal")
	public String orderPayPalIPN(Model model, HttpServletRequest request) {
		return "/";
	}
}
