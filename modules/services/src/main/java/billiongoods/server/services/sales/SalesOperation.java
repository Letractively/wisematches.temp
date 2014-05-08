package billiongoods.server.services.sales;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class SalesOperation implements Serializable {
	private final LocalDateTime stopSalesDate;
	private final LocalDateTime startSalesDate;

	public SalesOperation(LocalDateTime stopSalesDate, LocalDateTime startSalesDate) {
		if (stopSalesDate == null) {
			throw new NullPointerException("Stop sales date can't be null");
		}
		if (startSalesDate == null) {
			throw new NullPointerException("Start sales date can't be null");
		}
		if (stopSalesDate.isAfter(startSalesDate)) {
			throw new IllegalArgumentException("Stop sales date can't be after start sales date");
		}

		this.stopSalesDate = stopSalesDate;
		this.startSalesDate = startSalesDate;
	}

	/**
	 * Indicates that this sales operation is expired: sales start date is before than now.
	 *
	 * @return {@code true} is sales operation is expired; {@code false} - otherwise.
	 */
	public boolean isExpired() {
		return startSalesDate.isBefore(LocalDateTime.now());
	}

	/**
	 * Returns true if sales are closed at this moment.
	 *
	 * @return the {@code true} if sales are closed; {@code false} - otherwise
	 */
	public boolean isSalesClosed() {
		final LocalDateTime now = LocalDateTime.now();
		return now.isAfter(stopSalesDate) && now.isBefore(startSalesDate);
	}

	/**
	 * Returns date when sales will be stopped. This data can be as in past, if sales are stopped at this moment as in
	 * the future if sales are stopped over time.
	 *
	 * @return the date when sales were or will be stopped or {@code null} if there is no any planed sales stop.
	 */
	public LocalDateTime getStopSalesDate() {
		return stopSalesDate;
	}

	/**
	 * Returns date when sales will be started again. This data is in past only and is always null if there is no stop
	 * sales date.
	 *
	 * @return the date when sales will be started again or {@code null} if there is no any planed sales stop/start
	 */
	public LocalDateTime getStartSalesDate() {
		return startSalesDate;
	}
}