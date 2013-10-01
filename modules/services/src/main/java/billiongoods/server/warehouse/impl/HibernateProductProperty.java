package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Attribute;
import billiongoods.server.warehouse.AttributeType;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class HibernateProductProperty {
	@Column(name = "attributeId")
	private Integer attributeId;

	@Column(name = "dvalue")
	private Double dValue;

	@Column(name = "ivalue")
	private Integer iValue;

	@Column(name = "svalue")
	private String sValue;

	private static final Integer BOOLEAN_TRUE = 1;

	@Deprecated
	public HibernateProductProperty() {
	}

	public HibernateProductProperty(Attribute attribute, Object value) {
		this.attributeId = attribute.getId();
		switch (attribute.getAttributeType()) {
			case STRING:
			case UNKNOWN:
				this.sValue = (String) value;
				break;
			case INTEGER:
				this.iValue = (Integer) value;
				break;
			case DOUBLE:
				this.dValue = (Double) value;
				break;
			case BOOLEAN:
				this.iValue = (value != null && value == Boolean.TRUE) ? 1 : 0;
				break;
			default:
				throw new IllegalArgumentException("Unsupported attribute type: " + attribute);
		}
	}

	public Integer getAttributeId() {
		return attributeId;
	}

	public Double getDValue() {
		return dValue;
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
			case DOUBLE:
				return dValue;
			case BOOLEAN:
				return BOOLEAN_TRUE.equals(iValue);
			default:
				return sValue;
		}
	}
}