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
