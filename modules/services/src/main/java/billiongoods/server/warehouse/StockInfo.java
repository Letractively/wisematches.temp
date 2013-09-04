package billiongoods.server.warehouse;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface StockInfo {
	int getSold();

	Integer getRest();


	boolean isLimited();

	boolean isAvailable();


	Date getRestockDate();
}
