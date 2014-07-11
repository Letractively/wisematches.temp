package billiongoods.server.services.payment;

import billiongoods.core.Personality;
import billiongoods.core.account.Account;
import billiongoods.core.search.SearchManager;
import billiongoods.server.services.address.Address;
import billiongoods.server.services.basket.Basket;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderManager extends SearchManager<Order, OrderContext, Void> {
	void addOrderListener(OrderListener l);

	void removeOrderListener(OrderListener l);


	Order getOrder(Long id);

	Order getByToken(String token);

	Order getByReference(String reference);


	OrdersSummary getOrdersSummary();

	OrdersSummary getOrdersSummary(Personality principal);


	int importAccountOrders(Account account);

	/**
	 * Create new order based on specified basket.
	 *
	 * @param person       the person who has created the order or null if it's nemo
	 * @param basket       the basket with products.
	 * @param address      the shipping address.
	 * @param shipmentType the shipment type.
	 * @param track        is this order should be tracked or not. TODO: remove this functionality
	 * @return create order.
	 */
	Order create(Personality person, Basket basket, Address address, ShipmentType shipmentType, boolean track);

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
	 * Cancel all order. The order can be cancelled only if it's in {@link OrderState#ACCEPTED} state.
	 *
	 * @param orderId    the order id to be cancelled
	 * @param refundId   payment system unique refund id
	 * @param commentary appropriate commentary.
	 */
	Order cancel(Long orderId, String refundId, String commentary);

	/**
	 * Indicates that payment has been accepted and order is ready for processing.
	 *
	 * @param orderId   the order id
	 * @param payer     the payer email
	 * @param payerName the payer name
	 * @param payerNote the payer note
	 * @param paymentId the payment id
	 * @return updated order
	 */
	Order accept(Long orderId, String payer, String payerName, String payerNote, String paymentId);

	/**
	 * Indicates that payment has been rejected.
	 *
	 * @param orderId   the order id
	 * @param person    the person who has rejected payment
	 * @param paymentId the payment id
	 * @param note      the payment note
	 * @return updated order
	 */
	Order reject(Long orderId, String person, String paymentId, String note);

	/**
	 * Suspend all order. Order can be suspended due to invalid or not full information about the order. For example,
	 * delivery address.
	 *
	 * @param orderId    the order id to be suspended.
	 * @param commentary appropriate commentary.
	 */
	Order suspend(Long orderId, String commentary);


	/**
	 * Split specified order into set of separate parts (parcels).
	 *
	 * @param orderId  the order id to be processed
	 * @param parcelId the processing number. Must be unique in one order.
	 * @param items    the processing items tracking under specified number
	 * @return update order
	 */
	Order process(Long orderId, String parcelId, Integer... items);

	Order shipping(Long orderId, String parcelId, String tracking, String note);

	Order shipped(Long orderId, String parcelId, String tracking, String note);

	Order suspend(Long orderId, String parcelId, Date resume, String note);

	Order close(Long orderId, String parcelId, Date delivered, String note);


	/**
	 * Remove specified order from the system. All order related information will be removed as well.
	 *
	 * @param orderId the order id to be removed.
	 */
	Order remove(Long orderId);

	/**
	 * TODO: tracking ability should be disabled. PayPal address will be used instead.
	 */
	@Deprecated
	void setOrderTracking(Order order, boolean enable);
}
