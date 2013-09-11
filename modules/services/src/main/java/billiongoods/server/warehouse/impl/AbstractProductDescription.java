package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.ProductDescription;
import billiongoods.server.warehouse.ProductState;
import billiongoods.server.warehouse.StockInfo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@MappedSuperclass
public class AbstractProductDescription implements ProductDescription {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "name")
	private String name;

	@Column(name = "weight")
	private double weight;

	@Column(name = "categoryId")
	private Integer categoryId;

	@Column(name = "comment")
	private String commentary;

	@Column(name = "state")
	@Enumerated(EnumType.ORDINAL)
	private ProductState state = ProductState.DISABLED;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "amount", column = @Column(name = "price")),
			@AttributeOverride(name = "primordialAmount", column = @Column(name = "primordialPrice"))
	})
	private Price price;

	@Embedded
	private HibernateStockInfo stockInfo = new HibernateStockInfo();

	@Column(name = "registrationDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date registrationDate;

	@Column(name = "previewImageId")
	private String previewImageId;

	@OrderColumn(name = "position")
	@ElementCollection(fetch = FetchType.LAZY, targetClass = HibernateProductProperty.class)
	@CollectionTable(name = "store_product_property", joinColumns = @JoinColumn(name = "productId"))
	protected List<HibernateProductProperty> propertyIds = new ArrayList<>();

	public AbstractProductDescription() {
		registrationDate = new Date();
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public StockInfo getStockInfo() {
		return stockInfo;
	}

	@Override
	public ProductState getState() {
		return state;
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public Integer getCategoryId() {
		return categoryId;
	}

	@Override
	public Price getPrice() {
		return price;
	}

	@Override
	public Date getRegistrationDate() {
		return registrationDate;
	}

	@Override
	public String getPreviewImageId() {
		return previewImageId;
	}

	@Override
	public String getCommentary() {
		return commentary;
	}

	void setName(String name) {
		this.name = name;
	}

	void setState(ProductState state) {
		this.state = state;
		if (state == ProductState.ACTIVE || state == ProductState.PROMOTED) {
			this.registrationDate = new Date();
		}
	}

	void setCategory(Integer categoryId) {
		this.categoryId = categoryId;
	}

	void setWeight(double weight) {
		this.weight = weight;
	}

	void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	void setPrice(Price price) {
		this.price = price;
	}

	void setRestockInfo(Integer available, Date restockDate) {
		this.stockInfo.setRestockInfo(available, restockDate);
	}

	void setPreviewImageId(String previewImageId) {
		this.previewImageId = previewImageId;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractProductDescription)) return false;

		AbstractProductDescription that = (AbstractProductDescription) o;
		return !(id != null ? !id.equals(that.id) : that.id != null);
	}

	@Override
	public final int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AbstractProductDescription{");
		sb.append("id=").append(id);
		sb.append(", name='").append(name).append('\'');
		sb.append(", weight=").append(weight);
		sb.append(", categoryId=").append(categoryId);
		sb.append(", state=").append(state);
		sb.append(", price=").append(price);
		sb.append(", stockInfo=").append(stockInfo);
		sb.append(", registrationDate=").append(registrationDate);
		sb.append(", previewImageId='").append(previewImageId).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
