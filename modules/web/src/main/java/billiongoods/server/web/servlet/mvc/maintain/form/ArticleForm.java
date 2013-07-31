package billiongoods.server.web.servlet.mvc.maintain.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Arrays;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ArticleForm {
	private Long id;

	private Integer category;

	@NotEmpty(message = "maintain.article.name.err.blank")
	@Length(max = 100, message = "maintain.article.name.err.max")
	private String name;

	@NotEmpty(message = "maintain.article.desc.err.blank")
	private String description;

	private String previewImage;

	private List<String> viewImages;

	private float price;

	private Float primordialPrice;

	private String restockDate;

	private Long[] accessories;

	private Integer[] propertyIds;

	private String[] propertyValues;

	private Integer[] optionIds;

	private String[] optionValues;

	private float supplierPrice;

	private Float supplierPrimordialPrice;

	private String supplierReferenceId;

	private String supplierReferenceCode;


	public ArticleForm() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPreviewImage() {
		return previewImage;
	}

	public void setPreviewImage(String previewImage) {
		this.previewImage = previewImage;
	}

	public List<String> getViewImages() {
		return viewImages;
	}

	public void setViewImages(List<String> viewImages) {
		this.viewImages = viewImages;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public Float getPrimordialPrice() {
		return primordialPrice;
	}

	public void setPrimordialPrice(Float primordialPrice) {
		this.primordialPrice = primordialPrice;
	}

	public String getRestockDate() {
		return restockDate;
	}

	public void setRestockDate(String restockDate) {
		this.restockDate = restockDate;
	}

	public Long[] getAccessories() {
		return accessories;
	}

	public void setAccessories(Long[] accessories) {
		this.accessories = accessories;
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

	public Integer[] getOptionIds() {
		return optionIds;
	}

	public void setOptionIds(Integer[] optionIds) {
		this.optionIds = optionIds;
	}

	public String[] getOptionValues() {
		return optionValues;
	}

	public void setOptionValues(String[] optionValues) {
		this.optionValues = optionValues;
	}

	public float getSupplierPrice() {
		return supplierPrice;
	}

	public void setSupplierPrice(float supplierPrice) {
		this.supplierPrice = supplierPrice;
	}

	public Float getSupplierPrimordialPrice() {
		return supplierPrimordialPrice;
	}

	public void setSupplierPrimordialPrice(Float supplierPrimordialPrice) {
		this.supplierPrimordialPrice = supplierPrimordialPrice;
	}

	public String getSupplierReferenceId() {
		return supplierReferenceId;
	}

	public void setSupplierReferenceId(String supplierReferenceId) {
		this.supplierReferenceId = supplierReferenceId;
	}

	public String getSupplierReferenceCode() {
		return supplierReferenceCode;
	}

	public void setSupplierReferenceCode(String supplierReferenceCode) {
		this.supplierReferenceCode = supplierReferenceCode;
	}


	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ArticleForm{");
		sb.append("id=").append(id);
		sb.append(", category=").append(category);
		sb.append(", name='").append(name).append('\'');
		sb.append(", description='").append(description).append('\'');
		sb.append(", previewImage='").append(previewImage).append('\'');
		sb.append(", viewImages=").append(viewImages);
		sb.append(", price=").append(price);
		sb.append(", primordialPrice=").append(primordialPrice);
		sb.append(", restockDate='").append(restockDate).append('\'');
		sb.append(", accessories=").append(accessories);
		sb.append(", propertyIds=").append(Arrays.toString(propertyIds));
		sb.append(", propertyValues=").append(Arrays.toString(propertyValues));
		sb.append(", optionIds=").append(Arrays.toString(optionIds));
		sb.append(", optionValues=").append(Arrays.toString(optionValues));
		sb.append(", supplierPrice=").append(supplierPrice);
		sb.append(", supplierPrimordialPrice=").append(supplierPrimordialPrice);
		sb.append(", supplierReferenceId='").append(supplierReferenceId).append('\'');
		sb.append(", supplierReferenceCode='").append(supplierReferenceCode).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
