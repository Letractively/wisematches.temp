package billiongoods.server.services.payment;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface OrderParcel {
	Long getId();

	/**
	 * Returns parcel number. That can be internal ID of processing system.
	 *
	 * @return the parcel number.
	 */
	int getNumber();

	Date getTimestamp();

	ParcelState getState();


	String getRefundToken();

	Date getExpectedResume();


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
