package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.FilteringAbility;
import billiongoods.server.warehouse.FilteringSummary;
import billiongoods.server.warehouse.StoreAttribute;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultFilteringAbility implements FilteringAbility {
	private Map<StoreAttribute, List<FilteringSummary>> attributeListMap;

	public DefaultFilteringAbility(Map<StoreAttribute, List<FilteringSummary>> attributeListMap) {
		this.attributeListMap = attributeListMap;
	}

	@Override
	public Set<StoreAttribute> getAttributes() {
		return attributeListMap.keySet();
	}

	@Override
	public List<FilteringSummary> getFilteringItems(StoreAttribute attribute) {
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
