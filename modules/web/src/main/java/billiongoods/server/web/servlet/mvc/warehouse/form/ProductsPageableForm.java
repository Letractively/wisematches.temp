package billiongoods.server.web.servlet.mvc.warehouse.form;

import billiongoods.server.web.servlet.mvc.PageableForm;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ProductsPageableForm extends PageableForm {
	private String query = null;
	private String filter = null;
	private Integer category = null;


	public ProductsPageableForm() {
	}

	@Override
	public void initialize(int totalCount, int filteredCount) {
		final String sort = getSort();
		if (sort == null) {
			if (query != null) {
				setSort(SortingType.RELEVANCE.getCode());
			} else {
				setSort(SortingType.BESTSELLING.getCode());
			}
		}

		super.initialize(totalCount, filteredCount);
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
}