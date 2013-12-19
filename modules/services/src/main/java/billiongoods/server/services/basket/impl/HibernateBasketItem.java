package billiongoods.server.services.basket.impl;

import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.warehouse.AttributeManager;
import billiongoods.server.warehouse.ProductManager;
import billiongoods.server.warehouse.ProductPreview;
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

	@Column(name = "product")
	private Integer productId;

	@OrderColumn(name = "position")
	@ElementCollection(targetClass = HibernateBasketOption.class, fetch = FetchType.LAZY)
	@CollectionTable(name = "store_basket_option", joinColumns = {
			@JoinColumn(name = "basketId", referencedColumnName = "basket"),
			@JoinColumn(name = "basketItemId", referencedColumnName = "number")
	})
	private List<HibernateBasketOption> optionIds = new ArrayList<>();

	@Transient
	private ProductPreview product;

	@Transient
	private Collection<Property> options = new ArrayList<>();

	@Deprecated
	HibernateBasketItem() {
	}

	public HibernateBasketItem(ProductPreview product, Collection<Property> options, int quantity) {
		this.product = product;
		this.productId = product.getId();

		this.quantity = quantity;

		if (options != null) {
			this.options = options;
			for (Property option : options) {
				optionIds.add(new HibernateBasketOption(option.getAttribute(), (String) option.getValue()));
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

	@Override
	public double getAmount() {
		return product.getPrice().getAmount() * quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public ProductPreview getProduct() {
		return product;
	}

	@Override
	public Collection<Property> getOptions() {
		return options;
	}

	void associate(HibernateBasket basket, int number) {
		this.pk = new Pk(basket.getId(), number);
	}

	void initialize(ProductManager productManager, AttributeManager attributeManager) {
		if (this.product == null || !this.product.getId().equals(productId)) {
			this.product = productManager.getPreview(productId);
		}

		if (options.isEmpty()) {
			options.clear();
			for (HibernateBasketOption optionId : optionIds) {
				options.add(new Property(attributeManager.getAttribute(optionId.getAttributeId()), optionId.getValue()));
			}
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

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Pk pk = (Pk) o;

			if (number != pk.number) return false;
			if (basket != null ? !basket.equals(pk.basket) : pk.basket != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = number;
			result = 31 * result + (basket != null ? basket.hashCode() : 0);
			return result;
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateBasketItem{");
		sb.append("pk=").append(pk);
		sb.append(", quantity=").append(quantity);
		sb.append(", productId=").append(productId);
		sb.append(", optionIds=").append(optionIds);
		sb.append('}');
		return sb.toString();
	}
}
