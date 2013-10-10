package billiongoods.server.web.servlet.mvc.maintain.form;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class GroupForm {
	private Integer id;
	private String name;
	private String action;
	private Integer categoryId;

	public GroupForm() {
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

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
}
