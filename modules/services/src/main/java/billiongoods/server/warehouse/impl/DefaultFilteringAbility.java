package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Attribute;
import billiongoods.server.warehouse.FilteringAbility;
import billiongoods.server.warehouse.FilteringSummary;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultFilteringAbility implements FilteringAbility {
	private final int totalCount;
	private final int filteredCount;
	private final double minPrice;
	private final double maxPrice;
	private Map<Attribute, List<FilteringSummary>> attributeListMap;

	public DefaultFilteringAbility(int totalCount, int filteredCount, double minPrice, double maxPrice, Map<Attribute, List<FilteringSummary>> attributeListMap) {
		this.totalCount = totalCount;
		this.filteredCount = filteredCount;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.attributeListMap = attributeListMap;
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
	public Set<Attribute> getAttributes() {
		return attributeListMap.keySet();
	}

	@Override
	public FilteringSummary getFilteringItem(Attribute attribute, String code) {
		final List<FilteringSummary> filteringSummaries = attributeListMap.get(attribute);
		if (filteringSummaries == null) {
			return null;
		}
		for (FilteringSummary summary : filteringSummaries) {
			if (summary.getName().equals(code)) {
				return summary;
			}
		}
		return null;
	}

	@Override
	public List<FilteringSummary> getFilteringItems(Attribute attribute) {
		return attributeListMap.get(attribute);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("DefaultFilteringAbility{");
		sb.append("attributeListMap=").append(attributeListMap);
		sb.append('}');
		return sb.toString();
	}
}
