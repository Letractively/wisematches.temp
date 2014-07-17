package billiongoods.server.services.payment;

import billiongoods.core.Personality;
import billiongoods.core.account.Account;
import billiongoods.core.search.SearchManager;
import billiongoods.server.services.address.Address;
import billiongoods.server.services.basket.Basket;

import java.time.LocalDateTime;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderManager extends SearchManager<Order, OrderContext, Void> {
	void addOrderListener(OrderListener l);

	void removeOrderListener(OrderListener l);

	void addParcelListener(ParcelListener l);

	void removeParcelListener(ParcelListener l);


	Order getOrder(Long id);

	Order getByToken(String token);

	Order getByReference(String reference);


	/**
	 * Create new order based on specified basket.
	 *
	 * @param person       the person who has created the order or null if it's nemo
	 * @param basket       the basket with products.
	 * @param address      the shipping address.
	 * @param shipmentType the shipment type.
	 * @return create order.
	 */
	Order create(Personality person, Basket basket, Address address, ShipmentType shipmentType);

	/**
	 * Indicates that bill for the order has been placed.
	 *
	 * @param orderId the order id
	 * @param token   the bill id.
	 * @return updated order.
	 */
	Order bill(Long orderId, String token);

	/**
	 * Indicates that payment has been failed.
	 *
	 * @param orderId the order id
	 * @param reason  the filed reason.
	 * @return updated order
	 */
	Order failed(Long orderId, String reason);

	/**
	 * Indicates that payment has been failed.
	 *
	 * @param token  the payment token from {@link #bill(Long, String)} operation.
	 * @param reason the filed reason.
	 * @return updated order
	 */
	Order failed(String token, String reason);

	/**
	 * Indicates that payment has been accepted and order is ready for processing.
	 *
	 * @param orderId   the order id
	 * @param paymentId the payment id
	 * @param payer     the payer email
	 * @param payerName the payer name
	 * @param payerNote the payer note
	 * @param amount
	 * @return updated order
	 */
	Order accept(Long orderId, String paymentId, double amount, String payer, String payerName, String payerNote);

	/**
	 * Indicates that payment has been rejected.
	 *
	 * @param orderId   the order id
	 * @param paymentId the payment id
	 * @param payer     the payer id
	 * @param payerNote the payment note
	 * @return updated order
	 */
	Order reject(Long orderId, String paymentId, double amount, String payer, String payerName, String payerNote);

	/**
	 * Suspend all order. Order can be suspended due to invalid or not full information about the order. For example,
	 * delivery address.
	 *
	 * @param orderId the order id to be suspended.
	 * @param note    appropriate note.
	 */
	Order suspend(Long orderId, String note);

	/**
	 * Cancel all order. The order can be cancelled only if it's in {@link OrderState#ACCEPTED} state.
	 *
	 * @param orderId the order id to be cancelled
	 * @param note    appropriate note.
	 */
	Order cancel(Long orderId, String note);

	@Deprecated
	Order refund(Long orderId, double amount, String refundId, String note);


	/**
	 * Split specified order into set of separate parts (parcels).
	 * <p>
	 * This method can be invoked many times and each new execution will create new parcels in the order.
	 *
	 * @param orderId the order id
	 * @param parcels the list of parcels.
	 * @return updated order
	 * @throws IllegalArgumentException if not all product items are covered by executing this method.
	 *                                  It means that before you must be sure that all products from this
	 *                                  order are splitted by parcels.
	 */
	Order process(Long orderId, ParcelEntry... parcels);

	/**
	 * Moves parcel with specified id into shipping state.
	 *
	 * @param parcelId the parcel that should be updated
	 * @param tracking the shipping tracking number
	 * @param note     associated with this change note.
	 * @return updated order
	 * @throws java.lang.IllegalStateException if parcel is not in {@link ParcelState#PROCESSING} or {@link ParcelState#SUSPENDED} states.
	 */
	Order shipping(Long orderId, Long parcelId, String tracking, String note);

	/**
	 * Moves parcel with specified id into shipped state.
	 *
	 * @param parcelId the parcel that should be updated
	 * @param tracking the international tracking number
	 * @param note     associated with this change note.
	 * @return updated order
	 * @throws java.lang.IllegalStateException if parcel is not in {@link ParcelState#PROCESSING}, {@link ParcelState#SHIPPING} or {@link ParcelState#SUSPENDED} states.
	 */
	Order shipped(Long orderId, Long parcelId, String tracking, String note);

	/**
	 * Moves parcel with specified id into suspended state.
	 *
	 * @param parcelId the parcel that should be updated
	 * @param resume   expected date when the parcel processing will be resumed.
	 * @param note     associated with this change note.
	 * @return updated order
	 * @throws java.lang.IllegalStateException if parcel is not in {@link ParcelState#PROCESSING} state.
	 */
	Order suspend(Long orderId, Long parcelId, LocalDateTime resume, String note);

	/**
	 * Moves parcel with specified id into closed state.
	 *
	 * @param parcelId  the parcel that should be updated
	 * @param delivered the date when the parcel has been received by customer.
	 * @param note      associated with this change note.
	 * @return updated order
	 * @throws java.lang.IllegalStateException if parcel is not in {@link ParcelState#SHIPPED} state.
	 */
	Order close(Long orderId, Long parcelId, LocalDateTime delivered, String note);

	/**
	 * Moves parcel with specified id into cancelled state.
	 *
	 * @param parcelId the parcel that should be updated
	 * @param note     associated with this change note.
	 * @return updated order
	 * @throws java.lang.IllegalStateException if parcel is not in {@link ParcelState#PROCESSING} or {@link ParcelState#SUSPENDED} state.
	 */
	Order cancel(Long orderId, Long parcelId, String note);


	/**
	 * Remove specified order from the system. All order related information will be removed as well.
	 *
	 * @param orderId the order id to be removed.
	 */
	Order remove(Long orderId);


	OrdersSummary getOrdersSummary();

	OrdersSummary getOrdersSummary(Personality principal);


	int importAccountOrders(Account account);
}
