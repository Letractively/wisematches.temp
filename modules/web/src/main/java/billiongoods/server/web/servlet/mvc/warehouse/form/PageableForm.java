package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.core.search.Orders;
import billiongoods.core.search.Range;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PageableForm {
	private int page = 1;
	private int count = DEFAULT_COUNT_NUMBER;
	private String sort = DEFAULT_SORT.getCode();

	private String query = null;
	private String filter = null;
	private Integer category = null;

	private int totalCount;
	private int filteredCount;

	private Range range;

	private static final int DEFAULT_COUNT_NUMBER = 24;
	private static final SortingType DEFAULT_SORT = SortingType.BESTSELLING;
	private Orders orders;

	public PageableForm() {
	}

	public void initialize(int totalCount, int filteredCount) {
		if (page < 1) {
			page = 1;
		}
		if (count < 1) {
			count = DEFAULT_COUNT_NUMBER;
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
			this.orders = Orders.of(sortingType.getOrder());
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

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
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