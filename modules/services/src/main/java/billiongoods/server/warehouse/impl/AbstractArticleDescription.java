package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.ArticleState;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.StockInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@MappedSuperclass
public class AbstractArticleDescription implements ArticleDescription {
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
	private ArticleState state = ArticleState.DISABLED;

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

	public AbstractArticleDescription() {
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
	public ArticleState getState() {
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

	void setState(ArticleState state) {
		this.state = state;
		if (state == ArticleState.ACTIVE || state == ArticleState.PROMOTED) {
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
		if (!(o instanceof AbstractArticleDescription)) return false;

		AbstractArticleDescription that = (AbstractArticleDescription) o;
		return !(id != null ? !id.equals(that.id) : that.id != null);
	}

	@Override
	public final int hashCode() {
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AbstractArticleDescription{");
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
