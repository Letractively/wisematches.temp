package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Attribute;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_category")
public class HibernateCategory {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "parent")
	private Integer parentId;

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

	@Deprecated
	protected HibernateCategory() {
	}

	protected HibernateCategory(String name, String description, HibernateCategory parent, int position, Set<Attribute> attributes) {
		this.name = name;
		this.description = description;
		this.position = position;

		if (parent != null) {
			this.parentId = parent.id;
		}
		setAttributes(attributes);
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isActive() {
		return active;
	}

	public int getPosition() {
		return position;
	}

	public Integer getParentId() {
		return parentId;
	}

	public Set<Integer> getAttributeIds() {
		return attributeIds;
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

	void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	void setAttributes(Set<Attribute> attributes) {
		this.attributeIds.clear();

		if (attributes != null) {
			for (Attribute attribute : attributes) {
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
		final StringBuilder sb = new StringBuilder("HibernateCategory{");
		sb.append("id=").append(id);
		sb.append(", parentId=").append(parentId);
		sb.append(", name='").append(name).append('\'');
		sb.append(", description='").append(description).append('\'');
		sb.append(", position=").append(position);
		sb.append(", active=").append(active);
		sb.append(", attributeIds=").append(attributeIds);
		sb.append('}');
		return sb.toString();
	}
}
