package billiongoods.server.services.payment;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Order {
    Long getId();

    Long getBuyer();

    String getCode();

    float getShipment();

    float getTotalAmount();

    ShipmentType getShipmentType();

    Address getAddress();

    OrderState getOrderState();

    List<OrderItem> getOrderItems();
}
