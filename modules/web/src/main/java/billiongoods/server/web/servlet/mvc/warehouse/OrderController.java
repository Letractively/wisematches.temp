package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.paypal.PayPalException;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.ExpiredParametersException;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderErrorForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderViewForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.TrackingForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
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

import java.util.Locale;

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
        hideNavigation(model);

        if (form.isEmpty()) {
            return "/content/warehouse/order/track";
        }
        return processOrderStatus(form, errors, model);
    }

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
                model.addAttribute("order", order);
                model.addAttribute("orderIsNew", form.isPayPalRedirect());
                return "/content/warehouse/order/view";
            }
        }
        return "/content/warehouse/order/track";
    }

    @RequestMapping("/failed")
    public String internal(@ModelAttribute("form") OrderErrorForm form) throws PayPalException, ExpiredParametersException {
        if (form.getException() == null) {
            throw new ExpiredParametersException();
        }
        throw form.getException();
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

    @Autowired
    public void setOrderManager(OrderManager orderManager) {
        this.orderManager = orderManager;
    }
}
