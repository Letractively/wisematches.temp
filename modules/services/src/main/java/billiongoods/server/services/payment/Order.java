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

	Address getAddress();

	ShipmentType getShipmentType();


	Date getTimestamp();

	OrderState getOrderState();

	List<OrderItem> getOrderItems();


	String getPayer();

	boolean isTracking();

	String getComment();


	String getReferenceTracking();

	String getChinaMailTracking();

	String getInternationalTracking();


	List<OrderLog> getOrderLogs();
}
