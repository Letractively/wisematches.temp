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


	/**
	 * @deprecated moved to OrderPortion
	 */
	@Deprecated
	Date getTimestamp();

	/**
	 * @deprecated moved to OrderPortion
	 */
	@Deprecated
	OrderState getOrderState();


	/**
	 * @deprecated moved to OrderPortion
	 */
	@Deprecated
	int getItemsCount();

	/**
	 * @deprecated moved to OrderPortion
	 */
	@Deprecated
	List<OrderItem> getOrderItems();

	List<OrderPortion> getOrderPortions();

	String getPayer();

	String getPayerName();

	String getPayerNote();

	String getPaymentId();

	boolean isTracking();


	String getCoupon();

	String getCommentary();


	List<OrderLog> getOrderLogs();


	/**
	 * @deprecated moved to OrderPortion
	 */
	@Deprecated
	String getReferenceTracking();

	/**
	 * @deprecated moved to OrderPortion
	 */
	@Deprecated
	String getChinaMailTracking();

	/**
	 * @deprecated moved to OrderPortion
	 */
	@Deprecated
	String getInternationalTracking();


	/**
	 * @deprecated moved to OrderPortion
	 */
	@Deprecated
	String getRefundToken();

	/**
	 * @deprecated moved to OrderPortion
	 */
	@Deprecated
	Date getExpectedResume();
}
