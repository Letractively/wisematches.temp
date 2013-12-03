package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Attribute;
import billiongoods.server.warehouse.AttributeType;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class HibernateProductPropertyCollection {
	@Column(name = "attributeId")
	private Integer attributeId;

	@Column(name = "svalue")
	private String sValue;

	@Column(name = "ivalue")
	private Integer iValue;

	@Column(name = "bvalue")
	private Boolean bValue;

	@Deprecated
	HibernateProductPropertyCollection() {
	}

	public HibernateProductPropertyCollection(Attribute attribute, Object value) {
		this.attributeId = attribute.getId();

		if (value != null) {
			switch (attribute.getAttributeType()) {
				case STRING:
				case UNKNOWN:
					this.sValue = (String) value;
					break;
				case INTEGER:
					if (value instanceof String) {
						this.iValue = Integer.valueOf((String) value);
					} else {
						this.iValue = (Integer) value;
					}
					break;
				case BOOLEAN:
					if (value instanceof String) {
						this.bValue = Boolean.valueOf((String) value);
					} else {
						this.bValue = (Boolean) value;
					}
					break;
				default:
					throw new IllegalArgumentException("Unsupported attribute type: " + attribute);
			}
		}
	}

	public Integer getAttributeId() {
		return attributeId;
	}

	public Boolean getBValue() {
		return bValue;
	}

	public Integer getIValue() {
		return iValue;
	}

	public String getSValue() {
		return sValue;
	}

	public Object getValue(AttributeType type) {
		switch (type) {
			case INTEGER:
				return iValue;
			case BOOLEAN:
				return bValue;
			default:
				return sValue;
		}
	}
}