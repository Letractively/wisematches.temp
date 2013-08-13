package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.paypal.PayPalException;
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

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/order")
public class OrderController extends AbstractController {
    private OrderManager orderManager;

    public OrderController() {
    }

    @RequestMapping("/status")
    public String viewOrderStatus(@ModelAttribute("form") OrderViewForm form, Errors errors, Model model) {
        System.out.println(form);
//        if (orderId != null && orderToken != null) {
//            final Order order = orderManager.getOrder(orderId);
//            if (!order.getToken().equals(orderToken)) {
//                return "/content/warehouse/order/expired";
//            }
//            model.addAttribute("order", order);
//            return "/content/warehouse/order/status";
//        }
        return "/content/warehouse/order/status";
    }

    @RequestMapping("/failed")
    public String internal(@ModelAttribute("form") OrderErrorForm form, Model model) throws PayPalException, ExpiredParametersException {
        if (form.getException() == null) {
            throw new ExpiredParametersException();
        }
        throw form.getException();
    }

    @Autowired
    public void setOrderManager(OrderManager orderManager) {
        this.orderManager = orderManager;
    }
}
