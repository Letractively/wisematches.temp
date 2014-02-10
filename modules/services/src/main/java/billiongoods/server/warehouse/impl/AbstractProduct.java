package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.ProductPreview;
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
public class AbstractProduct implements ProductPreview {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "name")
	private String name;

	@Column(name = "symbolic")
	private String symbolic;

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

	@Column(name = "soldCount")
	private int soldCount;

	@Column(name = "recommended")
	private boolean recommended;

	@Embedded
	private StockInfo stockInfo = new StockInfo();

	@Embedded
	private HibernateSupplierInfo supplierInfo = new HibernateSupplierInfo();

	@Column(name = "registrationDate")
	@Temporal(TemporalType.TIMESTAMP)
	private Date registrationDate;

	@Column(name = "previewImageId")
	private String previewImageId;

	@OrderColumn(name = "position")
	@ElementCollection(fetch = FetchType.LAZY, targetClass = HibernateProductPropertyCollection.class)
	@CollectionTable(name = "store_product_property", joinColumns = @JoinColumn(name = "productId"))
	protected List<HibernateProductPropertyCollection> propertyIds = new ArrayList<>();

	public AbstractProduct() {
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
	public String getSymbolic() {
		return symbolic;
	}

	@Override
	public String getSymbolicUri() {
		return symbolic == null ? String.valueOf(id) : (symbolic + "-" + id);
	}

	@Override
	public StockInfo getStockInfo() {
		return stockInfo;
	}

	@Override
	public HibernateSupplierInfo getSupplierInfo() {
		return supplierInfo;
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
	public int getSoldCount() {
		return soldCount;
	}

	@Override
	public boolean isRecommended() {
		return recommended;
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

	void setName(String name, String symbolic) {
		this.name = name;
		this.symbolic = symbolic;
	}

	void setState(ProductState state) {
		final ProductState oldState = this.state;

		this.state = state;

		if ((state == ProductState.ACTIVE || state == ProductState.PROMOTED) &&
				(oldState != ProductState.ACTIVE && oldState != ProductState.PROMOTED)) {
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

	void setStockInfo(StockInfo info) {
		this.stockInfo = info;
	}

	void setRecommended(boolean recommended) {
		this.recommended = recommended;
	}

	void setPreviewImageId(String previewImageId) {
		this.previewImageId = previewImageId;
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AbstractProduct)) return false;

		AbstractProduct that = (AbstractProduct) o;
		return !(id != null ? !id.equals(that.id) : that.id != null);
	}

	@Override
	public final int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AbstractProduct{");
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
