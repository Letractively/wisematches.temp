package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Category;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_article")
public class HibernateArticleDescription extends AbstractArticleDescription {
	public HibernateArticleDescription() {
	}

	public HibernateArticleDescription(String name, boolean active, Category category, float sellPrice, float sellDiscount, Date restockDate, Date registrationDate) {
		super(name, active, category, sellPrice, sellDiscount, restockDate, registrationDate);
	}
}
