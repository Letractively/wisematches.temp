package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.Personality;
import billiongoods.server.services.ServerDescriptor;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketManager;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.payment.PaymentSystem;
import billiongoods.server.services.paypal.PayPalException;
import billiongoods.server.services.paypal.PayPalExpressCheckout;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderForm;
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

    private final WebAddressResolver addressResolver = new TheWebAddressResolver();

    private static final Logger log = LoggerFactory.getLogger("billiongoods.order.PayPalController");

    public PayPalController() {
    }

    @RequestMapping("")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String placeOrder(@ModelAttribute("order") OrderForm form, Errors errors, Model model) {
        final Personality principal = getPrincipal();
        final Basket basket = basketManager.getBasket(principal);

        final Order order = orderManager.createOrder(getPrincipal(), basket, form, PaymentSystem.PAY_PAL);

        log.info("Order for personal " + getPrincipal() + " has been created.");

        try {
            final String redirect = expressCheckout.initiateExpressCheckout(order, addressResolver);
            log.info("PayPal token has been generated: " + token);

            return "redirect:" +;
        } catch (PayPalException ex) {
            log.error("PayPal processing error: " + ex.getMessage(), ex);
            orderManager.deleteOrder(order.getId());

            errors.reject("error.internal");

            return "forward:/warehouse/basket/rollback";
        }
    }

    @RequestMapping("/accepted")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String orderAccepted(Model model, @RequestParam("token") String token, @RequestParam("PayerID") String payerId) {
        try {
            expressCheckout.approveExpressCheckout(token, payerId);
            return "/content/warehouse/order/accepted";
        } catch (PayPalException ex) {
            log.error("Payment can't be processed: " + token, ex);

            model.addAttribute("exception", ex);
            return "/content/warehouse/order/failed";
        }
    }

    @RequestMapping("/rejected")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String orderRejected(@RequestParam("token") String token) {
        // TODO: reject order by token and basket
        try {
            expressCheckout.rejectExpressCheckout(token);
        } catch (PayPalException ex) {
            log.error("Payment can't be processed: " + token, ex);
        }


        return "/content/warehouse/order/rejected";
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
