package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.StoreAttribute;

import javax.persistence.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_attribute")
@Deprecated
public class HibernateStoreStoreAttribute implements StoreAttribute {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "name")
	private String name;

	@Column(name = "unit")
	private String unit;

	@Deprecated
	HibernateStoreStoreAttribute() {
	}

	public HibernateStoreStoreAttribute(String name, String unit) {
		this.name = name;
		this.unit = unit;
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

	void setName(String name) {
		this.name = name;
	}

	void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		HibernateStoreStoreAttribute that = (HibernateStoreStoreAttribute) o;
		return !(id != null ? !id.equals(that.id) : that.id != null);
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "HibernateStoreStoreAttribute{" +
				"id=" + id +
				", name='" + name + '\'' +
				", unit='" + unit + '\'' +
				'}';
	}
}
