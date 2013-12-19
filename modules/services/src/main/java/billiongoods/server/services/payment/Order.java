package billiongoods.server.services.payment;

import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Order {
	Long getId();

	Long getPersonId();


	String getToken();

	double getAmount();

	double getDiscount();

	Shipment getShipment();


	Date getCreated();

	Date getShipped();

	Date getClosed();


	Date getTimestamp();

	OrderState getOrderState();


	int getItemsCount();

	List<OrderItem> getOrderItems();


	String getPayer();

	String getPayerName();

	String getPayerNote();

	String getPaymentId();

	boolean isTracking();


	String getCoupon();

	String getCommentary();


	List<OrderLog> getOrderLogs();


	String getReferenceTracking();

	String getChinaMailTracking();

	String getInternationalTracking();


	String getRefundToken();

	Date getExpectedResume();
}
