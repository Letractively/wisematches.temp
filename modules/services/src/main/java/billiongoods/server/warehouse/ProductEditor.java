package billiongoods.server.warehouse;

import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ProductEditor {
	String name;
	String symbolic;
	String description;
	Integer categoryId;
	Price price;
	double weight;
	Integer storeAvailable;
	Date restockDate;
	String previewImage;
	List<String> imageIds;
	List<Option> options;
	List<Property> properties;
	String referenceUri;
	String referenceCode;
	Supplier wholesaler;
	Price supplierPrice;
	String commentary;
	ProductState productState = ProductState.DISABLED;

	public ProductEditor() {
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

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Price getPrice() {
		return price;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Integer getStoreAvailable() {
		return storeAvailable;
	}

	public void setStoreAvailable(Integer storeAvailable) {
		this.storeAvailable = storeAvailable;
	}

	public Date getRestockDate() {
		return restockDate;
	}

	public void setRestockDate(Date restockDate) {
		this.restockDate = restockDate;
	}

	public String getPreviewImage() {
		return previewImage;
	}

	public void setPreviewImage(String previewImage) {
		this.previewImage = previewImage;
	}

	public List<String> getImageIds() {
		return imageIds;
	}

	public void setImageIds(List<String> imageIds) {
		this.imageIds = imageIds;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public String getReferenceUri() {
		return referenceUri;
	}

	public void setReferenceUri(String referenceUri) {
		this.referenceUri = referenceUri;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public Supplier getWholesaler() {
		return wholesaler;
	}

	public void setWholesaler(Supplier wholesaler) {
		this.wholesaler = wholesaler;
	}

	public Price getSupplierPrice() {
		return supplierPrice;
	}

	public void setSupplierPrice(Price supplierPrice) {
		this.supplierPrice = supplierPrice;
	}

	public String getCommentary() {
		return commentary;
	}

	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	public ProductState getProductState() {
		return productState;
	}

	public void setProductState(ProductState productState) {
		this.productState = productState;
	}
}