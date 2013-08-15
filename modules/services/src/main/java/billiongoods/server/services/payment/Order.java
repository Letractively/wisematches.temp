package billiongoods.server.services.payment;

import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Order {
    Long getId();

    Long getBuyer();


    String getToken();

    double getAmount();

    double getShipment();

    double getExchangeRate();

    Address getAddress();

    ShipmentType getShipmentType();

    Date getTimestamp();

    Date getCreationTime();

    OrderState getOrderState();

    List<OrderItem> getOrderItems();


    String getPayer();

    String getPayerNote();

    String getPaymentId();

    boolean isTracking();


    String getReferenceTracking();

    String getChinaMailTracking();

    String getInternationalTracking();

    String getFailureComment();

    List<OrderLog> getOrderLogs();
}
