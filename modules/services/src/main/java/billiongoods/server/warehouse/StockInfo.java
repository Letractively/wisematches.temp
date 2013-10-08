package billiongoods.server.warehouse;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface StockInfo {
	Date getRestockDate();

	Integer getAvailable();

	StockState getStockState();
}
