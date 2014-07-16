package billiongoods.server.services.payment;

import java.time.LocalDateTime;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Parcel {
	Long getId();

	/**
	 * Returns parcel number. That can be internal ID of processing system.
	 *
	 * @return the parcel number.
	 */
	int getNumber();


	/**
	 * Returns creation date for the order.
	 *
	 * @return the order's creation date.
	 */
	Timeline getTimeline();

	/**
	 * Current parcel state.
	 *
	 * @return the parcel state.
	 */
	ParcelState getState();


	String getRefundToken();

	LocalDateTime getExpectedResume();


	String getChinaMailTracking();

	String getInternationalTracking();


	/**
	 * Checks is specified item placed in this parcel
	 *
	 * @param item the item to be checked
	 * @return {@code true} if specified item is in this parce; {@code false} - otherwise
	 */
	boolean contains(OrderItem item);
}
