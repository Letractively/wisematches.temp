package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Attribute;
import billiongoods.server.warehouse.AttributeManager;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Genealogy;

import javax.persistence.*;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_category")
public class HibernateCategory implements Category {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "parent")
	private Integer parentId;

	@Transient
	private int level = -1;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "position")
	private int position;

	@Column(name = "active")
	private boolean active;

	@Column(name = "attributeId")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "store_category_attribute", joinColumns = @JoinColumn(name = "categoryId"))
	private Set<Integer> attributeIds = new HashSet<>();

	@Transient
	private Set<Attribute> attributes = new HashSet<>();

	@Transient
	private transient Genealogy genealogy;

	@Transient
	private transient HibernateCategory parent;

	@Transient
	private transient List<Category> children = new ArrayList<>();

	protected HibernateCategory() {
	}

	protected HibernateCategory(String name, String description, HibernateCategory parent, int position, Set<Attribute> attributes) {
		this.name = name;
		this.description = description;

		if (parent != null) {
			this.parentId = parent.id;
			preInit(parent);
		}
		this.position = position;

		setAttributes(attributes);
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public int getLevel() {
		if (level == -1) {
			int i = 0;
			Category p = parent;
			while (p != null) {
				i++;
				p = p.getParent();
			}
			level = i;
		}
		return level;
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
	public Category getParent() {
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
	public Set<Attribute> getAttributes() {
		return attributes;
	}

	@Override
	public int getPosition() {
		return position;
	}

	Integer getParentId() {
		return parentId;
	}

	void preInit(HibernateCategory parentCategory) {
		if (parentCategory == null && this.parentId == null) {
			return;
		}

		if (parentCategory == null || this.parentId == null || !parentCategory.id.equals(this.parentId)) {
			throw new IllegalArgumentException("Incorrect parent id");
		}
		this.parent = parentCategory;
		parentCategory.children.add(this);
	}

	void initialize(AttributeManager attributeManager) {
		Collections.sort(children, DefaultCatalog.COMPARATOR);

		for (Integer attributeId : attributeIds) {
			final Attribute attribute = attributeManager.getAttribute(attributeId);
			if (attribute == null) {
				throw new IllegalStateException("Unknown attribute with id: " + attributeId);
			}
			attributes.add(attribute);
		}
	}

	void setName(String name) {
		this.name = name;
	}

	void setDescription(String description) {
		this.description = description;
	}

	void setPosition(int position) {
		this.position = position;
	}

	void setActive(boolean active) {
		this.active = active;
	}

	void setParent(HibernateCategory parent) {
		if (this.parent != null) {
			this.parent.children.remove(this);
		}

		this.parent = parent;

		if (this.parent != null) {
			this.parentId = parent.getId();
			this.parent.children.add(this);
		} else {
			this.parentId = null;
		}
	}

	void setAttributes(Set<Attribute> attributes) {
		this.attributes.clear();
		this.attributeIds.clear();

		if (attributes != null) {
			for (Attribute attribute : attributes) {
				this.attributes.add(attribute);
				this.attributeIds.add(attribute.getId());
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof HibernateCategory)) return false;

		HibernateCategory category = (HibernateCategory) o;
		return !(id != null ? !id.equals(category.id) : category.id != null);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "HibernateCategory{" + "id=" + id + ", parentId=" + parentId + ", name='" + name + '\'' + ", position=" + position + ", active=" + active + ", childrenCount=" + children.size() + '}';
	}
}
