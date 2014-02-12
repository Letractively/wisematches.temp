package billiongoods.server.web.servlet.mvc.maintain.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class CategoryForm {
	private Integer id;

	private Integer parent;

	private int position;

	private Set<Integer> attributes;

	@NotEmpty(message = "maintain.category.name.err.blank")
	@Length(max = 50, message = "maintain.category.name.err.max")
	private String name;

	private String symbolic;

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

	public String getSymbolic() {
		return symbolic;
	}

	public void setSymbolic(String symbolic) {
		this.symbolic = symbolic;
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

	public Set<Integer> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<Integer> attributes) {
		this.attributes = attributes;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
