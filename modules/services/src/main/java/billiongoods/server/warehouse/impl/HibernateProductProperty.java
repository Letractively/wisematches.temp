package billiongoods.server.warehouse.impl;

import javax.persistence.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_product_property")
public class HibernateProductProperty {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "productId")
	private Integer productId;

	@Column(name = "position")
	private Integer position;
	@Column(name = "attributeId")
	private Integer attributeId;

	@Column(name = "svalue")
	private String sValue;

	@Column(name = "ivalue")
	private Integer iValue;

	@Column(name = "bvalue")
	private Boolean bValue;

	@Deprecated
	HibernateProductProperty() {
	}

	public Integer getId() {
		return id;
	}

	public Integer getProductId() {
		return productId;
	}
}
