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


	String getPayer();

	String getPayerName();

	String getPayerNote();

	String getPaymentId();

	boolean isTracking();


	String getCoupon();

	String getCommentary();


	/**
	 * Returns order items count.
	 *
	 * @return the items count.
	 */
	int getItemsCount();

	/**
	 * Returns all log messages for this order.
	 *
	 * @return the log messages for this ord
	 */
	List<OrderLog> getOrderLogs();

	/**
	 * Returns list of all items in this order.
	 *
	 * @return the list of all items in this order.
	 */
	List<OrderItem> getOrderItems();


	OrderParcel getParcel(int number);

	/**
	 * Returns list of order parcels. Can be empty if there is no any parcel yet. Can't be null.
	 *
	 * @return list of order parcels.
	 */
	List<OrderParcel> getOrderParcels();


	List<OrderItem> getParcelItems(OrderParcel parcel);


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
