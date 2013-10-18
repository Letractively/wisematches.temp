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

	Shipment getShipment();


	Date getTimestamp();

	Date getCreationTime();

	OrderState getOrderState();

	List<OrderItem> getOrderItems();


	String getPayer();

	String getPayerNote();

	String getPaymentId();

	boolean isTracking();


	String getCommentary();


	List<OrderLog> getOrderLogs();


	String getReferenceTracking();

	String getChinaMailTracking();

	String getInternationalTracking();


	String getRefundToken();

	Date getExpectedResume();
}
