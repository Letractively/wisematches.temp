package billiongoods.server.services.basket.impl;

import billiongoods.core.Personality;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.coupon.Coupon;
import billiongoods.server.warehouse.AttributeManager;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.ProductManager;

import javax.persistence.*;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_basket")
public class HibernateBasket implements Basket {
	@Id
	@Column(name = "pid", nullable = false, updatable = false, unique = true)
	private Long principal;

	@Column(name = "coupon")
	private String coupon;

	@Column(name = "creationTime", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTime;

	@Column(name = "updatingTime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatingTime;

	@Column(name = "expirationDays")
	private Integer expirationDays;

	@OrderBy("number")
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pk.basket", targetEntity = HibernateBasketItem.class)
	private List<BasketItem> basketItems = new ArrayList<>();

	public HibernateBasket() {
	}

	public HibernateBasket(Personality personality) {
		this(personality, null);
	}

	public HibernateBasket(Personality personality, Integer expirationDays) {
		this.principal = personality.getId();
		this.expirationDays = expirationDays;
		this.creationTime = this.updatingTime = new Date();
	}

	@Override
	public Long getId() {
		return principal;
	}

	@Override
	public double getAmount() {
		double res = .0d;
		for (BasketItem basketItem : basketItems) {
			res += basketItem.getAmount();
		}
		return Price.round(res);
	}

	@Override
	public double getWeight() {
		double res = .0d;
		for (BasketItem basketItem : basketItems) {
			res += basketItem.getProduct().getWeight();
		}
		return Price.round(res);
	}

	@Override
	public String getCoupon() {
		return coupon;
	}

	public Integer getExpirationDays() {
		return expirationDays;
	}

	@Override
	public Date getCreationTime() {
		return creationTime;
	}

	@Override
	public Date getUpdatingTime() {
		return updatingTime;
	}

	@Override
	public Iterator<BasketItem> iterator() {
		return basketItems.iterator();
	}

	@Override
	public List<BasketItem> getBasketItems() {
		return basketItems;
	}

	@Override
	public HibernateBasketItem getBasketItem(int number) {
		for (BasketItem basketItem : basketItems) {
			if (basketItem.getNumber() == number) {
				return (HibernateBasketItem) basketItem;
			}
		}
		return null;
	}

	void addBasketItem(HibernateBasketItem item) {
		item.associate(this, getAvailableItemIndex());
		basketItems.add(item);
		this.updatingTime = new Date();
	}

	void removeBasketItem(HibernateBasketItem item) {
		if (basketItems.remove(item)) {
			this.updatingTime = new Date();
		}
	}

	void setCoupon(Coupon coupon) {
		this.coupon = coupon == null ? null : coupon.getCode();
	}

	private int getAvailableItemIndex() {
		int i = 0;
		final int[] indexes = new int[basketItems.size()];
		for (BasketItem basketItem : basketItems) {
			indexes[i++] = basketItem.getNumber();
		}
		Arrays.sort(indexes);

		int p = 0;
		if (indexes.length == 0) {
			p = 0;
		} else if (indexes.length == 1) {
			p = indexes[0] + 1;
		} else {
			for (int j = 0; j < indexes.length - 1 && p == 0; j++) {
				int a = indexes[j];
				int b = indexes[j + 1];
				if (b - a != 1) {
					p = a + 1;
				}
			}

			if (p == 0) {
				p = indexes[indexes.length - 1] + 1;
			}
		}
		return p;
	}

	void initialize(ProductManager productManager, AttributeManager attributeManager) {
		for (BasketItem basketItem : basketItems) {
			((HibernateBasketItem) basketItem).initialize(productManager, attributeManager);
		}
	}

	boolean validate() {
		boolean res = false;
		for (Iterator<BasketItem> iterator = basketItems.iterator(); iterator.hasNext(); ) {
			BasketItem basketItem = iterator.next();
			if (basketItem.getProduct() == null) {
				res = true;
				iterator.remove();
			}
		}
		return res;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateBasket{");
		sb.append("principal=").append(principal);
		sb.append(", creationTime=").append(creationTime);
		sb.append(", updatingTime=").append(updatingTime);
		sb.append(", expirationDays=").append(expirationDays);
		sb.append(", basketItems=").append(basketItems);
		sb.append('}');
		return sb.toString();
	}
}
