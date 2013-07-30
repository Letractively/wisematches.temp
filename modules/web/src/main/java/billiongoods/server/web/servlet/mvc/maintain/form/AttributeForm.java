package billiongoods.server.web.servlet.mvc.maintain.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AttributeForm {
	private Integer id;

	@NotEmpty(message = "maintain.attr.name.err.blank")
	@Length(max = 45, message = "maintain.attr.name.err.max")
	private String name;

	@NotEmpty(message = "maintain.attr.unit.err.blank")
	private String unit;

	public AttributeForm() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
