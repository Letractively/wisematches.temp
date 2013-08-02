package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.AttributeManager;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.CategoryManager;

import javax.persistence.*;
import java.util.Date;
import java.util.Formatter;

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

	@Transient
	private String code;

	@Column(name = "soldCount")
	private int soldCount;

	@Column(name = "active")
	private boolean active;

	@Column(name = "categoryId")
	private Integer categoryId;

	@Transient
	private Category category;

	@Column(name = "price")
	private float price;

	@Column(name = "primordialPrice")
	private Float primordialPrice;

	@Column(name = "restockDate")
	private Date restockDate;

	@Column(name = "registrationDate")
	private Date registrationDate;

	@Column(name = "previewImageId")
	private String previewImageId;

	private static final StringBuilder sb = new StringBuilder();
	private static final Formatter FORMATTER = new Formatter(sb);

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
	public String getCode() {
		return code;
	}

	@Override
	public int getSoldCount() {
		return soldCount;
	}


	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public Category getCategory() {
		return category;
	}

	@Override
	public float getPrice() {
		return price;
	}

	@Override
	public Float getPrimordialPrice() {
		return primordialPrice;
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
	}

	void setCategory(Category category) {
		this.category = category;
		this.categoryId = category.getId();
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public void setPrimordialPrice(Float primordialPrice) {
		this.primordialPrice = primordialPrice;
	}

	void setRestockDate(Date restockDate) {
		this.restockDate = restockDate;
	}

	void setPreviewImageId(String previewImageId) {
		this.previewImageId = previewImageId;
	}

	void initialize(CategoryManager manager, AttributeManager attributeManager) {
		this.category = manager.getCategory(categoryId);
		synchronized (FORMATTER) {
			sb.setLength(0);
			code = FORMATTER.format("%06d", id).toString();
		}
	}

	void incrementSoldCount() {
		this.soldCount++;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AbstractArticleDescription{");
		sb.append("id=").append(id);
		sb.append(", name='").append(name).append('\'');
		sb.append(", active=").append(active);
		sb.append(", categoryId=").append(categoryId);
		sb.append(", price=").append(price);
		sb.append(", primordialPrice=").append(primordialPrice);
		sb.append(", registrationDate=").append(registrationDate);
		sb.append('}');
		return sb.toString();
	}
}
