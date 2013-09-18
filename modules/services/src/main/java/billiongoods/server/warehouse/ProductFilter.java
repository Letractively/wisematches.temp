package billiongoods.server.warehouse;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ProductFilter {
	private final Map<Attribute, List<String>> filter;

	public ProductFilter(Map<Attribute, List<String>> filter) {
		this.filter = filter;
	}

	public Set<Attribute> getAttributes() {
		return filter.keySet();
	}

	public List<String> getValues(Attribute attribute) {
		return filter.get(attribute);
	}

	public boolean isAllowed(Attribute attr, String value) {
		final List<String> strings = filter.get(attr);
		return strings != null && strings.contains(value);
	}
}
