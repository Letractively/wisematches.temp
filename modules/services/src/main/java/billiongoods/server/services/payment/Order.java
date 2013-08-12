package billiongoods.server.services.payment;

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

	ShipmentType getShipmentType();


	Address getAddress();

	OrderState getOrderState();

	List<OrderItem> getOrderItems();


	String getPayer();

	boolean isTracking();

	String getComment();


	String getReferenceTracking();

	String getChinaMailTracking();

	String getInternationalTracking();
}
