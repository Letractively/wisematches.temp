package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Price;

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

	@Column(name = "soldCount")
	private int soldCount;

	@Column(name = "active")
	private boolean active;

	@Column(name = "categoryId")
	private Integer categoryId;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "amount", column = @Column(name = "price")),
			@AttributeOverride(name = "primordialAmount", column = @Column(name = "primordialPrice"))
	})
	private Price price;

	@Column(name = "restockDate")
	@Temporal(TemporalType.DATE)
	private Date restockDate;

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
	public int getSoldCount() {
		return soldCount;
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public boolean isActive() {
		return active;
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
	public Date getRestockDate() {
		return restockDate;
	}

	@Override
	public Date getRegistrationDate() {
		return registrationDate;
	}

	@Override
	public String getPreviewImageId() {
		return previewImageId;
	}

	void setName(String name) {
		this.name = name;
	}

	void setActive(boolean active) {
		this.active = active;
		if (active) {
			this.registrationDate = new Date();
		}
	}

	void setCategory(Category category) {
		this.categoryId = category.getId();
	}

	void setWeight(double weight) {
		this.weight = weight;
	}

	public void setPrice(Price price) {
		this.price = price;
	}

	void setRestockDate(Date restockDate) {
		this.restockDate = restockDate;
	}

	void setPreviewImageId(String previewImageId) {
		this.previewImageId = previewImageId;
	}

	void incrementSoldCount() {
		this.soldCount++;
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
		sb.append(", active=").append(active);
		sb.append(", categoryId=").append(categoryId);
		sb.append(", price=").append(price);
		sb.append(", registrationDate=").append(registrationDate);
		sb.append('}');
		return sb.toString();
	}
}
