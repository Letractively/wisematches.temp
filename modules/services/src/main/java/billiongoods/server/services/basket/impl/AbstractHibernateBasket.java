package billiongoods.server.services.basket.impl;

import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
//@Entity
//@Table(name = "store_basket_member")
public class AbstractHibernateBasket implements Basket {
	@Id
	@Column(name = "pid", nullable = false, updatable = false, unique = true)
	private Long principal;

	@Column(name = "creationTime")
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTime;

	private List<BasketItem> basketItems = new ArrayList<>();

	public AbstractHibernateBasket() {
		this.creationTime = new Date();
	}

	@Override
	public Date getCreationTime() {
		return creationTime;
	}

	@Override
	public List<BasketItem> getBasketItems() {
		return basketItems;
	}

	public void addBasketItem(HibernateBasketItem item) {
		basketItems.add(item);
	}

	public void removeBasketItem(HibernateBasketItem item) {
		basketItems.remove(item);
	}
}
