package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Attribute;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class HibernateArticleProperty {
	@Column(name = "attributeId")
	private Integer attributeId;

	@Column(name = "value")
	private String value;

	public HibernateArticleProperty() {
	}

	public HibernateArticleProperty(Attribute attribute, String value) {
		this.attributeId = attribute.getId();
		this.value = value;
	}

	public Integer getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(Integer attributeId) {
		this.attributeId = attributeId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
