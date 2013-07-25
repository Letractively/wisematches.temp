package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.CategoryManager;

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
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "active")
	private boolean active;

	@Column(name = "categoryId")
	private Integer categoryId;

	@Transient
	private Category category;

	@Column(name = "sellPrice")
	private float sellPrice;

	@Column(name = "sellDiscount")
	private float sellDiscount;

	@Column(name = "restockDate")
	private Date restockDate;

	@Column(name = "registrationDate")
	private Date registrationDate;

	AbstractArticleDescription() {
	}

	public AbstractArticleDescription(String name, boolean active, Category category, float sellPrice, float sellDiscount, Date restockDate, Date registrationDate) {
		this.name = name;
		this.active = active;
		this.category = category;
		this.categoryId = category.getId();
		this.sellPrice = sellPrice;
		this.sellDiscount = sellDiscount;
		this.restockDate = restockDate;
		this.registrationDate = registrationDate;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
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
	public float getSellPrice() {
		return sellPrice;
	}

	@Override
	public float getSellDiscount() {
		return sellDiscount;
	}

	@Override
	public Date getRestockDate() {
		return restockDate;
	}

	@Override
	public Date getRegistrationDate() {
		return registrationDate;
	}

	void setName(String name) {
		this.name = name;
	}

	void setActive(boolean active) {
		this.active = active;
	}

	void setCategory(Category category) {
		this.category = category;
	}

	void setSellPrice(float sellPrice) {
		this.sellPrice = sellPrice;
	}

	void setSellDiscount(float sellDiscount) {
		this.sellDiscount = sellDiscount;
	}

	void setRestockDate(Date restockDate) {
		this.restockDate = restockDate;
	}

	void initialize(CategoryManager manager) {
		this.category = manager.getCategory(categoryId);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("AbstractArticleDescription{");
		sb.append("id=").append(id);
		sb.append(", active=").append(active);
		sb.append(", categoryId=").append(categoryId);
		sb.append(", sellPrice=").append(sellPrice);
		sb.append(", sellDiscount=").append(sellDiscount);
		sb.append('}');
		return sb.toString();
	}
}
