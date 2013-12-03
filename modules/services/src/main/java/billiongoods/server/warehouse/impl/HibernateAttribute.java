package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Attribute;
import billiongoods.server.warehouse.AttributeType;

import javax.persistence.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_attribute")
public class HibernateAttribute implements Attribute {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "name")
	private String name;

	@Column(name = "unit")
	private String unit;

	@Column(name = "description")
	private String description;

	@Column(name = "priority")
	private int priority;

	@Column(name = "type")
	private AttributeType attributeType;

	@Deprecated
	HibernateAttribute() {
	}

	public HibernateAttribute(String name, String unit, String description, AttributeType attributeType) {
		this.name = name;
		this.unit = unit;
		this.description = description;
		this.attributeType = attributeType;
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
	public String getUnit() {
		return unit;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public AttributeType getAttributeType() {
		return attributeType;
	}

	void setName(String name) {
		this.name = name;
	}

	void setUnit(String unit) {
		this.unit = unit;
	}

	void setDescription(String description) {
		this.description = description;
	}

	void setPriority(int priority) {
		this.priority = priority;
	}

	void setAttributeType(AttributeType attributeType) {
		this.attributeType = attributeType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HibernateAttribute that = (HibernateAttribute) o;
		return !(id != null ? !id.equals(that.id) : that.id != null);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "HibernateAttribute{" +
				"id=" + id +
				", name='" + name + '\'' +
				", unit='" + unit + '\'' +
				'}';
	}
}
