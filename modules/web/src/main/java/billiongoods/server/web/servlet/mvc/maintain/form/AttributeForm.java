package billiongoods.server.web.servlet.mvc.maintain.form;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AttributeForm {
	private Integer categoryId;
	private Integer attributeId;
	private String value;

	public AttributeForm() {
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
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
