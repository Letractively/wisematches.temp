package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Catalog;
import billiongoods.server.warehouse.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
		Collections.sort(this.rootCategories, COMPARATOR);
	}

	public void addRootCategory(HibernateCategory i) {
		this.rootCategories.add(i);
		Collections.sort(this.rootCategories, COMPARATOR);
	}

	public void removeRootCategory(HibernateCategory hc) {
		this.rootCategories.remove(hc);
	}

	static final Comparator<Category> COMPARATOR = new Comparator<Category>() {
		@Override
		public int compare(Category o1, Category o2) {
			return ((HibernateCategory) o1).getPosition() - ((HibernateCategory) o2).getPosition();
		}
	};
}