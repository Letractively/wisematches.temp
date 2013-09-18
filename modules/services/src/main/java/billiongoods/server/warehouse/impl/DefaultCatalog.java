package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.AttributeManager;
import billiongoods.server.warehouse.Catalog;
import billiongoods.server.warehouse.Category;

import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultCatalog implements Catalog {
	private final List<Category> rootCategories = new ArrayList<>();
	private final Map<Integer, DefaultCategory> categoryMap = new HashMap<>();

	public DefaultCatalog() {
	}

	@Override
	public DefaultCategory getCategory(Integer id) {
		return categoryMap.get(id);
	}

	@Override
	public List<Category> getRootCategories() {
		return rootCategories;
	}

	void initialize(List<HibernateCategory> list, AttributeManager attributeManager) {
		categoryMap.clear();
		rootCategories.clear();

		for (HibernateCategory category : list) {
			categoryMap.put(category.getId(), new DefaultCategory(category, attributeManager));
		}

		for (HibernateCategory category : list) {
			updateCategoryParent(categoryMap.get(category.getId()), category.getParentId());
		}
		Collections.sort(rootCategories, DefaultCategory.COMPARATOR);
	}

	DefaultCategory addCategory(HibernateCategory category, AttributeManager attributeManager) {
		DefaultCategory dc = new DefaultCategory(category, attributeManager);
		categoryMap.put(dc.getId(), dc);

		updateCategoryParent(dc, category.getParentId());
		return dc;
	}

	DefaultCategory updateCategory(HibernateCategory category, AttributeManager attributeManager) {
		final DefaultCategory defaultCategory = categoryMap.get(category.getId());

		defaultCategory.updateCategoryInfo(category, attributeManager);
		updateCategoryParent(defaultCategory, category.getParentId());
		return defaultCategory;
	}

	private void updateCategoryParent(DefaultCategory dc, Integer parentId) {
		if (dc.getParent() == null) {
			rootCategories.remove(dc);
		} else {
			dc.getParent().removeChild(dc);
		}

		final DefaultCategory parent = categoryMap.get(parentId);
		if (parent != null) {
			parent.addChild(dc);
		} else {
			rootCategories.add(dc);
			Collections.sort(rootCategories, DefaultCategory.COMPARATOR);
		}
	}
}