package billiongoods.server.web.servlet.mvc.maintain.form;

import billiongoods.server.warehouse.GroupType;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class GroupForm {
	private Integer id;
	private String name;
	private String action;
	private GroupType type = GroupType.MODE;
	private Integer categoryId;

	public GroupForm() {
	}

	public GroupForm(Integer id) {
		this.id = id;
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

	public GroupType getType() {
		return type;
	}

	public void setType(GroupType type) {
		this.type = type;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
}
