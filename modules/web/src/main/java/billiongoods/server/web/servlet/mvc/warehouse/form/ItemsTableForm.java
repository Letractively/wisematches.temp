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
    private String query = null;
    private boolean inactive = false;

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

        int k = (int) Math.round((totalCount / (double) count) + 0.5d);
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

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setItemSortType(ItemSortType itemSortType) {
        this.sort = itemSortType.getName();
        this.itemSortType = itemSortType;
    }

    public ItemSortType getItemSortType() {
        return itemSortType;
    }

    public Range createRange() {
        return Range.limit((page - 1) * count, count);
    }

    public boolean isInactive() {
        return inactive;
    }

    public void setInactive(boolean inactive) {
        this.inactive = inactive;
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

    public void disableSorting() {
        sort = "";
    }
}