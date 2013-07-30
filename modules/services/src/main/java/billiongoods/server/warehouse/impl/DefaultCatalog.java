package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Catalog;
import billiongoods.server.warehouse.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultCatalog implements Catalog {
	private List<Category> rootCategories = new ArrayList<>();

	public DefaultCatalog() {
	}

	@Override
	public List<Category> getRootCategories() {
		return rootCategories;
	}

	public void setRootCategories(List<Category> rootCategories) {
		this.rootCategories = rootCategories;
	}

	public void addRootCategory(HibernateCategory i) {
		rootCategories.add(i);
	}

	public void removeRootCategory(HibernateCategory hc) {
		rootCategories.remove(hc);
	}
}
