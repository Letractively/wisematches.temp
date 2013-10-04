package billiongoods.server.warehouse;

import java.util.List;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface FilteringAbility {
	int getTotalCount();

	double getMinPrice();

	double getMaxPrice();

	Set<Attribute> getAttributes();

	FilteringSummary getFilteringItem(Attribute attribute, String code);

	List<FilteringSummary> getFilteringItems(Attribute attribute);
}
