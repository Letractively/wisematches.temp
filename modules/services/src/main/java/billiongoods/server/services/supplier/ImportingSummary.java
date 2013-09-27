package billiongoods.server.services.supplier;

import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Property;

import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ImportingSummary {
	Date getStartDate();

	Date getFinishDate();


	int getTotalCount();

	int getBrokenCount();

	int getSkippedCount();

	int getImportedCount();


	Category getImportingCategory();

	List<Integer> getGroups();

	List<Property> getProperties();
}
