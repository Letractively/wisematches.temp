package billiongoods.server.warehouse;

import java.util.List;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface FilteringAbility {
	Set<Attribute> getAttributes();

	List<FilteringSummary> getFilteringItems(Attribute attribute);
}
