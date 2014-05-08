package billiongoods.server.web.servlet.mvc.maintain.form;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ShowcaseForm {
	private Integer section;
	private Integer position;
	private Integer category;
	private String name;
	private boolean arrival;
	private boolean subCategories;

	public ShowcaseForm() {
	}

	public Integer getSection() {
		return section;
	}

	public void setSection(Integer section) {
		this.section = section;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isArrival() {
		return arrival;
	}

	public void setArrival(boolean arrival) {
		this.arrival = arrival;
	}

	public boolean isSubCategories() {
		return subCategories;
	}

	public void setSubCategories(boolean subCategories) {
		this.subCategories = subCategories;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ShowcaseForm{");
		sb.append("section=").append(section);
		sb.append(", position=").append(position);
		sb.append(", category=").append(category);
		sb.append(", name='").append(name).append('\'');
		sb.append(", arrival=").append(arrival);
		sb.append(", subCategories=").append(subCategories);
		sb.append('}');
		return sb.toString();
	}
}
