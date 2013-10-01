package billiongoods.server.warehouse;

import java.util.List;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface FilteringAbility {
	double getMinPrice();

	double getMaxPrice();

	Set<Attribute> getAttributes();

	int getValue(Attribute attribute, String code);

	boolean hasValue(Attribute attribute, String code);

	List<FilteringSummary> getFilteringItems(Attribute attribute);
}
