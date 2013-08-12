package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.payment.ShipmentType;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PaymentInfo {
    private float order;
    private ShipmentType shipment;

    public static final float REGISTERED_SHIPMENT_AMOUNT = 1.70f;
    public static final float FREE_REGISTERED_SHIPMENT_AMOUNT = 25.f;

    public PaymentInfo(Basket basket, ShipmentType shipment) {
        float order = .0f;
        if (basket != null) {
            for (BasketItem item : basket) {
                order += item.getArticle().getPrice() * item.getQuantity();
            }
        }

        this.order = order;
        this.shipment = shipment;
    }

    public float getOrder() {
        return order;
    }

    public float getTotal() {
        return getOrder() + getShipment();
    }

    public float getShipment() {
        return (order >= FREE_REGISTERED_SHIPMENT_AMOUNT || shipment == ShipmentType.FREE ? 0.f : REGISTERED_SHIPMENT_AMOUNT);
    }

    public float getAmountForRegistered() {
        return FREE_REGISTERED_SHIPMENT_AMOUNT - order;
    }

    public boolean isFreeRegisteredShipment() {
        return order >= FREE_REGISTERED_SHIPMENT_AMOUNT;
    }
}
