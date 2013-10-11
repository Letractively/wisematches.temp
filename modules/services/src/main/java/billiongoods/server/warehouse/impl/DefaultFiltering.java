package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Filtering;
import billiongoods.server.warehouse.FilteringItem;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultFiltering implements Filtering {
	private final int totalCount;
	private final int filteredCount;
	private final double minPrice;
	private final double maxPrice;
	private final List<FilteringItem> filteringItems;

	public DefaultFiltering(int totalCount, int filteredCount, double minPrice, double maxPrice, List<FilteringItem> filteringItems) {
		this.totalCount = totalCount;
		this.filteredCount = filteredCount;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.filteringItems = filteringItems;
	}

	@Override
	public int getTotalCount() {
		return totalCount;
	}

	@Override
	public int getFilteredCount() {
		return filteredCount;
	}

	@Override
	public double getMinPrice() {
		return minPrice;
	}

	@Override
	public double getMaxPrice() {
		return maxPrice;
	}

	@Override
	public List<FilteringItem> getFilteringItems() {
		return filteringItems;
	}
}
