package billiongoods.server.warehouse;

import java.util.Map;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ProductFilter {
	private final Double minPrice;
	private final Double maxPrice;
	private final Map<Attribute, FilteringValue> filter;

	public ProductFilter(Double minPrice, Double maxPrice, Map<Attribute, FilteringValue> filter) {
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.filter = filter;
	}

	/*
	public ProductFilter(Double minPrice, Double maxPrice, Map<Attribute, List<String>> filter) {
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.filter = filter;
	}
*/

	public boolean isEmpty() {
		if (filter == null || filter.isEmpty()) {
			return true;
		}

		for (FilteringValue filteringValue : filter.values()) {
			if (!filteringValue.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public Double getMinPrice() {
		return minPrice;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public Set<Attribute> getAttributes() {
		return filter.keySet();
	}

	public boolean hasValue(Attribute attribute) {
		final FilteringValue filteringValue = filter.get(attribute);
		return filteringValue != null && !filteringValue.isEmpty();
	}

	public FilteringValue getValue(Attribute attribute) {
		return filter.get(attribute);
	}

/*

	public List<String> getValues(Attribute attribute) {
		return filter.get(attribute);
	}

	public boolean isAllowed(Attribute attr, String value) {
		final List<String> strings = filter.get(attr);
		return strings != null && strings.contains(value);
	}
*/
}
