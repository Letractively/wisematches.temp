package billiongoods.server.services.basket.impl;

import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.Property;

import javax.persistence.Transient;
import java.util.Collection;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateBasketItem implements BasketItem {
	private Integer id;
	private int quantity;
	private Integer articleId;
	private Collection<Property> options;

	@Transient
	private ArticleDescription article;

	public HibernateBasketItem() {
	}

	public HibernateBasketItem(ArticleDescription article, List<Property> options, int quantity) {
		this.article = article;
		this.articleId = article.getId();
		this.options = options;
		this.quantity = quantity;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public int getQuantity() {
		return quantity;
	}

	@Override
	public ArticleDescription getArticle() {
		return article;
	}

	@Override
	public Collection<Property> getOptions() {
		return options;
	}
}
