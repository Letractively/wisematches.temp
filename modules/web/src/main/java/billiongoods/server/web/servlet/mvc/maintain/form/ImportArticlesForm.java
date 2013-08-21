package billiongoods.server.web.servlet.mvc.maintain.form;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ImportArticlesForm {
	private Integer category;
	private CommonsMultipartFile images;
	private CommonsMultipartFile description;

	public ImportArticlesForm() {
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
}
