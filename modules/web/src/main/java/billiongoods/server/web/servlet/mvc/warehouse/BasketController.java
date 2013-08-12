package billiongoods.server.web.servlet.mvc.warehouse;

import billiongoods.core.Personality;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.basket.BasketManager;
import billiongoods.server.services.payment.Order;
import billiongoods.server.services.payment.OrderManager;
import billiongoods.server.services.payment.Shipment;
import billiongoods.server.services.payment.ShipmentType;
import billiongoods.server.warehouse.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.warehouse.form.BasketItemForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.OrderForm;
import billiongoods.server.web.servlet.mvc.warehouse.form.PaymentInfo;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/warehouse/basket")
public class BasketController extends AbstractController {
    private OrderManager orderManager;
    private BasketManager basketManager;
    private ArticleManager articleManager;
    private AttributeManager attributeManager;

    private static final CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();

    public BasketController() {
    }

    @RequestMapping("rollback")
    public String rollbackOrder(@ModelAttribute("order") OrderForm form, Errors errors, Model model) {
        model.addAttribute("rollback", Boolean.TRUE);
        return viewBasket(form, errors, model);
    }

    @RequestMapping(value = {""}, method = RequestMethod.GET)
    public String viewBasket(@ModelAttribute("order") OrderForm form, Errors errors, Model model) {
        form.setNotifications(true);

        final Basket basket = basketManager.getBasket(getPrincipal());
        final PaymentInfo paymentInfo = new PaymentInfo(basket, ShipmentType.FREE);

        if (form.getShipment() == null) {
            if (paymentInfo.isFreeRegisteredShipment()) {
                form.setShipment(ShipmentType.REGISTERED);
            } else {
                form.setShipment(ShipmentType.FREE);
            }
        }

        hideNavigation(model);
        model.addAttribute("basket", basket);
        model.addAttribute("payment", paymentInfo);

        return "/content/warehouse/basket";
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String processBasket(@ModelAttribute("order") OrderForm form, Errors errors, Model model) {
        final Personality principal = getPrincipal();
        final Basket basket = basketManager.getBasket(principal);

        final Set<Integer> numbers = new HashSet<>();
        for (BasketItem basketItem : basket.getBasketItems()) {
            numbers.add(basketItem.getNumber());
        }
        final Integer[] itemNumbers = form.getItemNumbers();
        for (Integer itemNumber : itemNumbers) {
            numbers.remove(itemNumber);
        }

        for (Integer number : numbers) {
            basketManager.removeBasketItem(principal, number);
        }

        for (int i = 0; i < form.getItemNumbers().length; i++) {
            final Integer number = form.getItemNumbers()[i];
            final int quantity = form.getItemQuantities()[i];

            if (quantity != basket.getBasketItem(number).getQuantity()) {
                basketManager.updateBasketItem(principal, number, quantity);
            }
        }

        checkAddressField("name", form.getName(), errors);
        checkAddressField("city", form.getCity(), errors);
        checkAddressField("region", form.getRegion(), errors);
        checkAddressField("postalCode", form.getPostalCode(), errors);
        checkAddressField("streetAddress", form.getStreetAddress(), errors);

        if (!form.getPostalCode().matches("\\d{6}+")) {
            errors.rejectValue("postalCode", "basket.error.postalCode.format");
        }

        final ShipmentType shipmentType = form.getShipment();
        if (shipmentType == null) {
            errors.rejectValue("shipment", "basket.error.shipment.empty");
        }

        final PaymentInfo paymentInfo = new PaymentInfo(basket, form.getShipment());
        final Shipment shipment = new Shipment(paymentInfo.getShipment(), form, shipmentType);

        if (errors.hasErrors()) {
            hideNavigation(model);
            model.addAttribute("basket", basket);
            model.addAttribute("payment", paymentInfo);
            return "/content/warehouse/basket";
        }

        final Order order = orderManager.create(getPrincipal(), basket, shipment, form.isNotifications());
        return "forward:/warehouse/paypal/checkout?order=" + order.getId();
    }

    private void checkAddressField(String name, String value, Errors errors) {
        if (value == null || value.trim().isEmpty() || !asciiEncoder.canEncode(value)) {
            errors.rejectValue(name, "basket.error." + name + ".empty");
        }
    }

    @RequestMapping("add.ajax")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ServiceResponse addToBasket(@RequestBody BasketItemForm form, Locale locale) {
        try {
            final List<Property> options = new ArrayList<>();

            final Integer[] optionIds = form.getOptionIds();
            final String[] optionValues = form.getOptionValues();
            if (optionIds != null) {
                for (int i = 0; i < optionIds.length; i++) {
                    final Integer optionId = optionIds[i];
                    if (optionId != null) {
                        final Attribute attribute = attributeManager.getAttribute(optionId);
                        if (attribute == null) {
                            return responseFactory.failure("unknown.attribute", new Object[]{optionIds[i]}, locale);
                        }
                        options.add(new Property(attribute, optionValues[i]));
                    }
                }
            }

            final ArticleDescription article = articleManager.getDescription(form.getArticle());
            if (article == null) {
                return responseFactory.failure("unknown.article", new Object[]{form.getArticle()}, locale);
            }

            final int quantity = form.getQuantity();
            if (quantity <= 0) {
                return responseFactory.failure("illegal.quantity", new Object[]{quantity}, locale);
            }

            basketManager.addBasketItem(getPrincipal(), article, options, quantity);

            return responseFactory.success();
        } catch (Exception ex) {
            return responseFactory.failure("internal.error", locale);
        }
    }

    @RequestMapping("update.ajax")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ServiceResponse updateBasket(@RequestParam("n") Integer number, @RequestParam("q") int quantity) {

        return responseFactory.success();
    }

    @RequestMapping("remove.ajax")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ServiceResponse removeFromBasket(@RequestParam("n") Integer number) {

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
    public void setArticleManager(ArticleManager articleManager) {
        this.articleManager = articleManager;
    }

    @Autowired
    public void setAttributeManager(AttributeManager attributeManager) {
        this.attributeManager = attributeManager;
    }
}
