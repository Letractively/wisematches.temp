package billiongoods.server.services.sales;

import java.time.LocalDateTime;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface SalesOperationManager {
	/**
	 * Returns current sales operation info or {@code null} if there is no any sales restrictions.
	 *
	 * @return current sales operation info or {@code null} if there is no any sales restrictions.
	 */
	SalesOperation getSalesOperation();


	/**
	 * Indicates that sales can be started right now without waiting planed start date.
	 * <p>
	 * Does nothing is sales already allowed
	 */
	void openSales();

	/**
	 * Create new planed sales stop between specified dates
	 *
	 * @param stopSales  the date when sales should be stopped
	 * @param startSales the date when sales should be started again
	 */
	void closeSales(LocalDateTime stopSales, LocalDateTime startSales);
}