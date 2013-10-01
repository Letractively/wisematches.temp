package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Attribute;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class HibernateProductOption {
	@Column(name = "attributeId")
	private Integer attributeId;

	@Column(name = "value")
	private String value;

	@Deprecated
	HibernateProductOption() {
	}

	public HibernateProductOption(Attribute attribute, String value) {
		this.attributeId = attribute.getId();
		this.value = value;
	}

	public Integer getAttributeId() {
		return attributeId;
	}

	public String getValue() {
		return value;
	}
}
