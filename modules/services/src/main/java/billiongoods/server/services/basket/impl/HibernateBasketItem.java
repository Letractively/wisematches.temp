package billiongoods.server.services.basket.impl;

import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.ArticleManager;
import billiongoods.server.warehouse.AttributeManager;
import billiongoods.server.warehouse.Property;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_basket_item")
public class HibernateBasketItem implements BasketItem {
	@EmbeddedId
	private Pk pk;

	@Column(name = "quantity")
	private int quantity;

	@Column(name = "article")
	private Integer articleId;

	@OrderColumn(name = "position")
	@ElementCollection(targetClass = HibernateBasketOption.class)
	@CollectionTable(name = "store_basket_option", joinColumns = {
			@JoinColumn(name = "basketId", referencedColumnName = "basket"),
			@JoinColumn(name = "basketItemId", referencedColumnName = "number")
	})
	private List<HibernateBasketOption> optionIds = new ArrayList<>();

	@Transient
	private ArticleDescription article;

	@Transient
	private Collection<Property> options = new ArrayList<>();

	public HibernateBasketItem() {
	}

	public HibernateBasketItem(ArticleDescription article, List<Property> options, int quantity) {
		this.article = article;
		this.articleId = article.getId();

		this.quantity = quantity;

		if (options != null) {
			this.options = options;
			for (Property option : options) {
				optionIds.add(new HibernateBasketOption(option.getAttribute(), option.getValue()));
			}
		}
	}

	Long getBasket() {
		return pk.basket;
	}

	@Override
	public int getNumber() {
		return pk.number;
	}

	@Override
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public ArticleDescription getArticle() {
		return article;
	}

	@Override
	public Collection<Property> getOptions() {
		return options;
	}

	void associate(HibernateBasket basket, int number) {
		this.pk = new Pk(basket.getId(), number);
	}

	void initialize(ArticleManager articleManager, AttributeManager attributeManager) {
		this.article = articleManager.getDescription(articleId);

		for (HibernateBasketOption optionId : optionIds) {
			options.add(new Property(attributeManager.getAttribute(optionId.getAttributeId()), optionId.getValue()));
		}
	}

	@Embeddable
	public static class Pk implements Serializable {
		@Column(name = "number")
		private int number;

		@Column(name = "basket")
		private Long basket;

		public Pk() {
		}

		public Pk(Long basket, int number) {
			this.number = number;
			this.basket = basket;
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateBasketItem{");
		sb.append("pk=").append(pk);
		sb.append(", quantity=").append(quantity);
		sb.append(", articleId=").append(articleId);
		sb.append(", optionIds=").append(optionIds);
		sb.append('}');
		return sb.toString();
	}
}
