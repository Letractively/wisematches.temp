package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.*;
import billiongoods.server.warehouse.Character;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
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
	public List<Option> geOptions() {
		return null;
	}

	@Override
	public List<Long> getAccessories() {
		return null;
	}

	@Override
	public List<Character> getCharacters() {
		return null;
	}

	@Override
	public SupplierInfo getSupplierInfo() {
		return supplierInfo;
	}

	void incrementSoldCount() {
		this.soldCount++;
	}
}
