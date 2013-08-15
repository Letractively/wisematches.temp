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

	float getAmount();

	float getShipment();

	float getExchangeRate();

	Address getAddress();

	ShipmentType getShipmentType();

	Date getTimestamp();

	Date getCreationTime();

	OrderState getOrderState();

	List<OrderItem> getOrderItems();


	String getPayer();

	String getPaymentId();

	String getComment();

	boolean isTracking();


	String getReferenceTracking();

	String getChinaMailTracking();

	String getInternationalTracking();


	List<OrderLog> getOrderLogs();
}
