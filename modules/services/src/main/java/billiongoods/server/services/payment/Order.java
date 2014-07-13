package billiongoods.server.services.payment;

import java.util.Date;
import java.util.List;

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
     * Returns creation date for the order.
     *
     * @return the order's creation date.
     */
    Date getCreated();

    /**
     * Returns time when the order was updated last time.
     *
     * @return the time when the order was updated last time.
     */
    Date getTimestamp();

    /**
     * Returns final amount for this order.
     *
     * @return the final amount for this order.
     */
    double getAmount();

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
     * Returns current order state.
     *
     * @return the current order state.
     */
    OrderState getState();

    /**
     * Returns information about shimplent type for this order.
     *
     * @return the shimplent type for the order.
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
    List<OrderItem> getItems(OrderParcel parcel);


    /**
     * Returns list of order parcels. Can be empty if there is no any parcel yet. Can't be null.
     *
     * @return list of order parcels.
     */
    List<OrderParcel> getParcels();
}
