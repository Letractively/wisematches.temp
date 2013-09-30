package billiongoods.server.services.supplier.impl;

import billiongoods.server.services.supplier.ImportingSummary;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Property;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultImportingSummary implements ImportingSummary {
	private final Date startDate;
	private final int totalCount;
	private final Category category;

	private final List<Integer> groups = new ArrayList<>();
	private final List<Property> properties = new ArrayList<>();

	private int brokenCount;
	private int skippedCount;
	private int importedCount;

	private Date finishedDate = null;

	public DefaultImportingSummary(Category category, List<Property> properties, List<Integer> groups, int totalCount) {
		this.startDate = new Date();
		this.totalCount = totalCount;
		this.category = category;
		if (properties != null) {
			this.properties.addAll(properties);
		}
		if (groups != null) {
			this.groups.addAll(groups);
		}
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Date getFinishDate() {
		return finishedDate;
	}

	@Override
	public int getTotalCount() {
		return totalCount;
	}

	@Override
	public int getBrokenCount() {
		return brokenCount;
	}

	@Override
	public int getSkippedCount() {
		return skippedCount;
	}

	@Override
	public int getImportedCount() {
		return importedCount;
	}

	@Override
	public Category getImportingCategory() {
		return category;
	}

	@Override
	public List<Integer> getGroups() {
		return groups;
	}

	@Override
	public List<Property> getProperties() {
		return properties;
	}

	public void incrementSkipped() {
		skippedCount++;
	}

	public void incrementBroken() {
		brokenCount++;
	}

	public void incrementImported() {
		importedCount++;
	}

	public void finalizeSummary() {
		finishedDate = new Date();
	}
}
