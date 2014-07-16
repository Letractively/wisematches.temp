package billiongoods.server.services.payment;

import java.time.temporal.Temporal;

/**
 * Returns order or parcel processing timeline.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Timeline {
	/**
	 * Time when parcel or order has been created.
	 *
	 * @return the time when parcel or order has been created.
	 */
	Temporal getCreated();

	/**
	 * Time when order or parcel processing has been started. It can be time when order or parcel has been paid.
	 *
	 * @return the time when order or parcel processing has been started
	 */
	Temporal getStarted();

	/**
	 * Time when order or parcel has been placed on supplier side but wasn't sent yet. Difference between
	 * created time and processed time is time for reaction on our side.
	 *
	 * @return the time when order has been placed on supplier side
	 */
	Temporal getProcessed();

	/**
	 * Time when order or parcel has been shipped (it doesn't matter, shipping or shipped state is used here).
	 *
	 * @return the time when order or parcel has been shipped.
	 */
	Temporal getShipped();

	/**
	 * Time when order or parcel timeline is finished. An order or parcel is finished if it was moved to any of
	 * final state: {@link OrderState#isFinalState()}
	 *
	 * @return time when order or parcel is finished.
	 */
	Temporal getFinished();
}
