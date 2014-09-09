package billiongoods.server.services.payment;

import java.time.temporal.Temporal;
import java.util.List;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Order {
	/**
	 * Returns unique order id.
	 *
	 * @return the unique order id.
	 */
	Long getId();

	/**
	 * Returns internal person account id who placed this order.
	 *
	 * @return the person id.
	 */
	Long getPersonId();

	/**
	 * Returns final amount for this order.
	 *
	 * @return the final amount for this order.
	 */
	double getAmount();

	/**
	 * Returns grand total amount for this order. If amount + shipment.amount - discount.amount
	 *
	 * @return the grand total amount for this order.
	 */
	double getGrandTotal();

	/**
	 * Returns current order state.
	 *
	 * @return the current order state.
	 */
	OrderState getState();

	/**
	 * Returns total number of products in the order. It can be more then items count
	 * because an item can have more than one product quantity.
	 * <p>
	 * This value is calculated like sum of quantities of all items.
	 *
	 * @return the total number of products in the order.
	 */
	int getProductsCount();

	/**
	 * Returns shop commentary for this order.
	 *
	 * @return the shop commentary for this order.
	 */
	String getCommentary();

	/**
	 * Returns creation date for the order.
	 *
	 * @return the order's creation date.
	 */
	Timeline getTimeline();

	/**
	 * Returns information about shipment type for this order.
	 *
	 * @return the shipment type for the order.
	 */
	Shipment getShipment();

	/**
	 * Returns detailed payment information for this order.
	 *
	 * @return the order payment information.
	 */
	OrderPayment getPayment();


	/**
	 * Returns discount information for the order.
	 *
	 * @return return discount information for the order.
	 */
	OrderDiscount getDiscount();

	/**
	 * Returns all log messages for this order.
	 *
	 * @return the log messages for this ord
	 */
	List<OrderLog> getLogs();


	/**
	 * Returns list of all items in this order.
	 *
	 * @return the list of all items in this order.
	 */
	List<OrderItem> getItems();

	/**
	 * Returns list of items which were placed only in specified parcel
	 *
	 * @param parcel the parcel to be filtered
	 * @return the list of items which were placed only in specified parcel
	 */
	List<OrderItem> getItems(Parcel parcel);

	/**
	 * Checks is this order has any associated international tracking number or not. This method doesn't check
	 * any shipment type or order state. It just checks original tracking numbers.
	 *
	 * @return {@code true} is there is any international tracking number; {@code false} - otherwise.
	 */
	boolean isTracking();

	/**
	 * Returns all available national tracking number for this order.
	 *
	 * @return all available national tracking number for this order.
	 */
	Set<String> getNationalTracking();

	/**
	 * Returns all international tracking numbers for this order.
	 *
	 * @return all international tracking numbers for this order.
	 */
	Set<String> getInternationalTracking();


	/**
	 * Returns list of order parcels. Can be empty if there is no any parcel yet. Can't be null.
	 *
	 * @return list of order parcels.
	 */
	List<Parcel> getParcels();

	/**
	 * Returns parcel by specified id if it belongs to this order.
	 *
	 * @param parcelId the parcel id
	 * @return the parcel by specified id or {@code null} if parcel doesn't exist.
	 */
	Parcel getParcel(Long parcelId);


	/**
	 * Returns time when this order has been changed last time.
	 *
	 * @return time when this order has been changed last time.
	 */
	Temporal getTimestamp();
}
