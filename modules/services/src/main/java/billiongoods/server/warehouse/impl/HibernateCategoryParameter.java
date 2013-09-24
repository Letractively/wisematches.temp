package billiongoods.server.warehouse.impl;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_category_parameter")
public class HibernateCategoryParameter {
	@Id
	@Column(name = "id", nullable = false, updatable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "categoryId")
	private Integer categoryId;

	@Column(name = "attributeId")
	private Integer attributeId;

	@ElementCollection
	@Column(name = "value")
	@CollectionTable(name = "store_category_parameter_value", joinColumns = @JoinColumn(name = "parameterId"))
	private Set<String> values = new HashSet<>();

	@Deprecated
	public HibernateCategoryParameter() {
	}

	public HibernateCategoryParameter(Integer categoryId, Integer attributeId) {
		this.categoryId = categoryId;
		this.attributeId = attributeId;
	}

	public Integer getAttributeId() {
		return attributeId;
	}

	public Set<String> getValues() {
		return values;
	}

	public boolean addValue(String value) {
		return values.add(value);
	}

	public boolean removeValue(String value) {
		return values.remove(value);
	}
}
