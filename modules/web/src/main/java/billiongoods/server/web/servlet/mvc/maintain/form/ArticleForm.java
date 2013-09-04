package billiongoods.server.web.servlet.mvc.maintain.form;

import billiongoods.server.warehouse.ArticleState;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.RelationshipType;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ArticleForm {
	private Integer id;

	private Integer category;

	@NotEmpty(message = "maintain.article.name.err.blank")
	@Length(max = 100, message = "maintain.article.name.err.max")
	private String name;

	@NotEmpty(message = "maintain.article.desc.err.blank")
	private String description;

	private String previewImage;

	private Collection<String> viewImages;

	private List<String> enabledImages;

	private double price;

	private Double primordialPrice;

	private double weight;

	private String restockDate;

	private Integer[] participatedGroups;

	private Integer[] relationshipGroups;

	private RelationshipType[] relationshipTypes;

	private Integer[] propertyIds;

	private String[] propertyValues;

	private Integer[] optionIds;

	private String[] optionValues;

	private double supplierPrice;

	private Double supplierPrimordialPrice;

	private String supplierReferenceId;

	private String supplierReferenceCode;

	private Integer storeAvailable;

	private String commentary;

	private ArticleState articleState;

	public ArticleForm() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public Collection<String> getViewImages() {
		return viewImages;
	}

	public void setViewImages(Collection<String> viewImages) {
		this.viewImages = viewImages;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Double getPrimordialPrice() {
		return primordialPrice;
	}

	public void setPrimordialPrice(Double primordialPrice) {
		this.primordialPrice = primordialPrice;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getRestockDate() {
		return restockDate;
	}

	public void setRestockDate(String restockDate) {
		this.restockDate = restockDate;
	}

	public Integer[] getParticipatedGroups() {
		return participatedGroups;
	}

	public void setParticipatedGroups(Integer[] participatedGroups) {
		this.participatedGroups = participatedGroups;
	}

	public Integer[] getRelationshipGroups() {
		return relationshipGroups;
	}

	public void setRelationshipGroups(Integer[] relationshipGroups) {
		this.relationshipGroups = relationshipGroups;
	}

	public RelationshipType[] getRelationshipTypes() {
		return relationshipTypes;
	}

	public void setRelationshipTypes(RelationshipType[] relationshipTypes) {
		this.relationshipTypes = relationshipTypes;
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

	public double getSupplierPrice() {
		return supplierPrice;
	}

	public void setSupplierPrice(double supplierPrice) {
		this.supplierPrice = supplierPrice;
	}

	public Double getSupplierPrimordialPrice() {
		return supplierPrimordialPrice;
	}

	public void setSupplierPrimordialPrice(Double supplierPrimordialPrice) {
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

	public List<String> getEnabledImages() {
		return enabledImages;
	}

	public void setEnabledImages(List<String> enabledImages) {
		this.enabledImages = enabledImages;
	}

	public boolean isCreating() {
		return id == null;
	}

	public Price createPrice() {
		return new Price(price, primordialPrice);
	}

	public Price createSupplierPrice() {
		return new Price(supplierPrice, supplierPrimordialPrice);
	}

	public Integer getStoreAvailable() {
		return storeAvailable;
	}

	public void setStoreAvailable(Integer storeAvailable) {
		this.storeAvailable = storeAvailable;
	}

	public String getCommentary() {
		return commentary;
	}

	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	public ArticleState getArticleState() {
		return articleState;
	}

	public void setArticleState(ArticleState articleState) {
		this.articleState = articleState;
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
		sb.append(", participatedGroups=").append(Arrays.toString(participatedGroups));
		sb.append(", relationshipGroups=").append(Arrays.toString(relationshipGroups));
		sb.append(", relationshipTypes=").append(Arrays.toString(relationshipTypes));
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
