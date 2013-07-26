package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Article;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Character;
import billiongoods.server.warehouse.Option;
import org.hibernate.annotations.IndexColumn;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_article")
public class HibernateArticle extends AbstractArticleDescription implements Article {
	@Column(name = "soldCount")
	private int soldCount;

	@Column(name = "description")
	private String description;

	@Column(name = "imageId")
	@IndexColumn(name = "order")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "store_article_image", joinColumns = @JoinColumn(name = "aid"))
	private List<String> imageIds = new ArrayList<>();

	@Column(name = "accessoryId")
	@IndexColumn(name = "order")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "store_article_accessories", joinColumns = @JoinColumn(name = "aid"))
	private List<Long> accessories = new ArrayList<>();

	@Embedded
	private HibernateSupplierInfo supplierInfo = new HibernateSupplierInfo();

	public HibernateArticle() {
	}

	public HibernateArticle(String name, boolean active, Category category, float sellPrice, float sellDiscount, Date restockDate, Date registrationDate, int soldCount, HibernateSupplierInfo supplierInfo) {
		super(name, active, category, sellPrice, sellDiscount, restockDate, registrationDate);
		this.soldCount = soldCount;
		this.supplierInfo = supplierInfo;
	}

	@Override
	public int getSoldCount() {
		return soldCount;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public List<Option> geOptions() {
		return null;
	}

	@Override
	public List<String> getImageIds() {
		return imageIds;
	}

	@Override
	public List<Long> getAccessories() {
		return accessories;
	}

	@Override
	public List<Character> getCharacters() {
		return null;
	}

	@Override
	public HibernateSupplierInfo getSupplierInfo() {
		return supplierInfo;
	}

	void incrementSoldCount() {
		this.soldCount++;
	}

	void setDescription(String description) {
		this.description = description;
	}
}
