package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.core.search.Range;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ItemsTableForm {
	private int page = 1;
	private int count = DEFAULT_COUNT_NUMBER;
	private int totalCount;

	private ItemSortType itemSortType;
	private String sort = "bestselling";

	private static final int DEFAULT_COUNT_NUMBER = 24;
	private static final ItemSortType DEFAULT_SORT = ItemSortType.BESTSELLING;

	public ItemsTableForm() {
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

	public int getTotalCount() {
		return totalCount;
	}

	public void validateForm(int totalCount) {
		if (page < 1) {
			page = 1;
		}
		if (count < 1) {
			count = DEFAULT_COUNT_NUMBER;
		}

		int k = Math.round((totalCount / (float) count) + 0.5f);
		if (page > k) {
			page = k;
		}

		this.totalCount = totalCount;

		itemSortType = DEFAULT_SORT;
		for (ItemSortType t : ItemSortType.values()) {
			if (t.getName().equals(sort)) {
				itemSortType = t;
				break;
			}
		}
		sort = itemSortType.getName();
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public ItemSortType getItemSortType() {
		return itemSortType;
	}

	public Range createRange() {
		return Range.limit((page - 1) * count, count);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ItemsTableForm{");
		sb.append("page=").append(page);
		sb.append(", count=").append(count);
		sb.append(", totalCount=").append(totalCount);
		sb.append('}');
		return sb.toString();
	}
}