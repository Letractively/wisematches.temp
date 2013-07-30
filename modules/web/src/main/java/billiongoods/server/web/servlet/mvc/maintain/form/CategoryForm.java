package billiongoods.server.web.servlet.mvc.maintain.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class CategoryForm {
	private Integer id;

	private Integer parent;

	private int position;

	private Integer[] attributes;

	@NotEmpty(message = "maintain.category.name.err.blank")
	@Length(max = 45, message = "maintain.category.name.err.max")
	private String name;

	@NotEmpty(message = "maintain.category.desc.err.blank")
	private String description;

	public CategoryForm() {
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getParent() {
		return parent;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}

	public Integer[] getAttributes() {
		return attributes;
	}

	public void setAttributes(Integer[] attributes) {
		this.attributes = attributes;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
