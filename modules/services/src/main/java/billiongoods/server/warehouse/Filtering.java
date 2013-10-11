package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Filtering {
	int getTotalCount();

	int getFilteredCount();


	double getMinPrice();

	double getMaxPrice();

	List<FilteringItem> getFilteringItems();
}
