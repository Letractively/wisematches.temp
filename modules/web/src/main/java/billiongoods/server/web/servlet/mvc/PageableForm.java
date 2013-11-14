package billiongoods.server.web.servlet.mvc;

import billiongoods.core.search.Orders;
import billiongoods.core.search.Range;
import billiongoods.server.web.servlet.mvc.warehouse.form.SortingType;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PageableForm {
	private int page;
	private int count;
	private String sort;

	private int totalCount;
	private int filteredCount;

	private Range range;
	private Orders orders;

	public PageableForm() {
	}

	public void initialize(int totalCount, int filteredCount) {

		if (page < 1) {
			page = 1;
		}
		if (count < 1) {
			count = 24;
		}

		int k = (int) Math.round((filteredCount / (double) count) + 0.5d);
		if (page > k) {
			page = k;
		}

		this.orders = null;
		this.totalCount = totalCount;
		this.filteredCount = filteredCount;
		this.range = Range.limit((page - 1) * count, count);

		final SortingType sortingType = SortingType.byCode(sort);
		if (sortingType != null) {
			this.orders = sortingType.getOrders();
		}
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public Range getRange() {
		return range;
	}

	public Orders getOrders() {
		return orders;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public int getFilteredCount() {
		return filteredCount;
	}

	public void disableSorting() {
		sort = "";
	}
}
