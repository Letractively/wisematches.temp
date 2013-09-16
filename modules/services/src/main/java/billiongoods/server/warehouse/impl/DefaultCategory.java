package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Genealogy;
import billiongoods.server.warehouse.StoreAttribute;
import billiongoods.server.warehouse.StoreAttributeManager;

import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultCategory implements Category {
	private Integer id;
	private String name;
	private int position;
	private boolean active;
	private String description;
	private Set<StoreAttribute> attributes = new HashSet<>();

	protected Genealogy genealogy;
	protected DefaultCategory parent;
	protected final List<Category> children = new ArrayList<>();

	protected DefaultCategory(HibernateCategory category, StoreAttributeManager manager) {
		updateCategoryInfo(category, manager);
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public boolean isFinal() {
		return children.size() == 0;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public DefaultCategory getParent() {
		return parent;
	}

	@Override
	public Genealogy getGenealogy() {
		if (genealogy == null) {
			genealogy = new Genealogy(this);
		}
		return genealogy;
	}

	@Override
	public List<Category> getChildren() {
		return children;
	}

	@Override
	public Set<StoreAttribute> getAttributes() {
		return attributes;
	}

	@Override
	public int getPosition() {
		return position;
	}

	void updateCategoryInfo(HibernateCategory category, StoreAttributeManager manager) {
		this.id = category.getId();
		this.name = category.getName();
		this.description = category.getDescription();
		this.position = category.getPosition();
		this.active = category.isActive();

		final Set<Integer> attributeIds = category.getAttributeIds();
		for (Integer attributeId : attributeIds) {
			attributes.add(manager.getAttribute(attributeId));
		}
	}

	void addChild(DefaultCategory category) {
		if (this.children.contains(category)) {
			return;
		}

		if (this.children.add(category)) {
			category.parent = this;
			category.genealogy = null;
			Collections.sort(this.children, COMPARATOR);
		}
	}

	void removeChild(DefaultCategory category) {
		if (!this.children.contains(category)) {
			return;
		}

		if (this.children.remove(category)) {
			category.parent = null;
			category.genealogy = null;
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DefaultCategory)) return false;

		DefaultCategory that = (DefaultCategory) o;
		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("DefaultCategory{");
		sb.append("id=").append(id);
		sb.append(", name='").append(name).append('\'');
		sb.append(", position=").append(position);
		sb.append(", active=").append(active);
		sb.append(", description='").append(description).append('\'');
		sb.append(", attributes=").append(attributes);
		sb.append('}');
		return sb.toString();
	}

	static final Comparator<Category> COMPARATOR = new Comparator<Category>() {
		@Override
		public int compare(Category o1, Category o2) {
			return ((DefaultCategory) o1).position - ((DefaultCategory) o2).position;
		}
	};
}
