package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.core.search.Range;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ItemsTableForm {
	private int page = 0;
	private int count = 8;
	private int totalCount;

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

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public Range createRange() {
		return Range.limit(page * count, count);
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