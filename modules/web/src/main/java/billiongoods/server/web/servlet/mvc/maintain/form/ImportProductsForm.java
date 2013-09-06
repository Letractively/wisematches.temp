package billiongoods.server.web.servlet.mvc.maintain.form;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ImportProductsForm {
	private Integer category;

	private Integer[] propertyIds;
	private String[] propertyValues;

	private Integer[] participatedGroups;

	private CommonsMultipartFile images;
	private CommonsMultipartFile description;

	private boolean validatePrice;

	public ImportProductsForm() {
	}

	public Integer getCategory() {
		return category;
	}

	public void setCategory(Integer category) {
		this.category = category;
	}

	public CommonsMultipartFile getImages() {
		return images;
	}

	public void setImages(CommonsMultipartFile images) {
		this.images = images;
	}

	public CommonsMultipartFile getDescription() {
		return description;
	}

	public void setDescription(CommonsMultipartFile description) {
		this.description = description;
	}

	public Integer[] getPropertyIds() {
		return propertyIds;
	}

	public void setPropertyIds(Integer[] propertyIds) {
		this.propertyIds = propertyIds;
	}

	public String[] getPropertyValues() {
		return propertyValues;
	}

	public void setPropertyValues(String[] propertyValues) {
		this.propertyValues = propertyValues;
	}

	public Integer[] getParticipatedGroups() {
		return participatedGroups;
	}

	public void setParticipatedGroups(Integer[] participatedGroups) {
		this.participatedGroups = participatedGroups;
	}

	public boolean isValidatePrice() {
		return validatePrice;
	}

	public void setValidatePrice(boolean validatePrice) {
		this.validatePrice = validatePrice;
	}
}
