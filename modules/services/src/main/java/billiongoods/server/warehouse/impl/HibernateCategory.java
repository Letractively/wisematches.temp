package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Attribute;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

	@Column(name = "symbolic")
	private String symbolic;

	@Column(name = "description")
	private String description;

	@Column(name = "position")
	private int position;

	@Column(name = "active")
	private boolean active;

	@JoinColumn(name = "categoryId")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<HibernateCategoryParameter> parameters = new ArrayList<>();

	@Deprecated
	protected HibernateCategory() {
	}

	protected HibernateCategory(String name, String symbolic, String description, Integer parentId, int position) {
		this.name = name;
		this.symbolic = symbolic;
		this.description = description;
		this.position = position;
		this.parentId = parentId;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getSymbolic() {
		return symbolic;
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

	void setName(String name) {
		this.name = name;
	}

	public void setSymbolic(String symbolic) {
		this.symbolic = symbolic;
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

	List<HibernateCategoryParameter> getParameters() {
		return parameters;
	}

	boolean addParameter(Integer attribute) {
		for (HibernateCategoryParameter parameter : parameters) {
			if (parameter.getAttributeId().equals(attribute)) {
				return false;
			}
		}

		parameters.add(new HibernateCategoryParameter(id, attribute));
		return true;
	}

	boolean removeParameter(Integer attribute) {
		for (Iterator<HibernateCategoryParameter> iterator = parameters.iterator(); iterator.hasNext(); ) {
			HibernateCategoryParameter parameter = iterator.next();
			if (parameter.getAttributeId().equals(attribute)) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}

	boolean addParameterValue(Attribute attribute, String value) {
		for (HibernateCategoryParameter parameter : parameters) {
			if (parameter.getAttributeId().equals(attribute.getId())) {
				return parameter.addValue(value);
			}
		}
		return false;
	}

	boolean removeParameterValue(Attribute attribute, String value) {
		for (HibernateCategoryParameter parameter : parameters) {
			if (parameter.getAttributeId().equals(attribute.getId())) {
				return parameter.removeValue(value);
			}
		}
		return false;
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
		sb.append('}');
		return sb.toString();
	}
}
