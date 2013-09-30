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
	private Map<Attribute, List<FilteringSummary>> attributeListMap;

	public DefaultFilteringAbility(Map<Attribute, List<FilteringSummary>> attributeListMap) {
		this.attributeListMap = attributeListMap;
	}

	@Override
	public Set<Attribute> getAttributes() {
		return attributeListMap.keySet();
	}

	@Override
	public int getValue(Attribute attribute, String code) {
		final List<FilteringSummary> filteringSummaries = attributeListMap.get(attribute);
		if (filteringSummaries == null) {
			return 0;
		}
		for (FilteringSummary summary : filteringSummaries) {
			if (summary.getName().equals(code)) {
				return summary.getCount();
			}
		}
		return 0;
	}

	@Override
	public boolean hasValue(Attribute attribute, String code) {
		return getValue(attribute, code) > 0;
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
