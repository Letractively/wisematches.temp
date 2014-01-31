package billiongoods.server.services.payment.impl;

import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.payment.OrderItem;
import billiongoods.server.warehouse.ProductPreview;
import billiongoods.server.warehouse.Property;
import billiongoods.server.warehouse.impl.HibernateProductPreview;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_order_item")
public class HibernateOrderItem implements OrderItem {
	@EmbeddedId
	private Pk pk;

	@JoinColumn(name = "product")
	@OneToOne(targetEntity = HibernateProductPreview.class, fetch = FetchType.LAZY)
	private ProductPreview product;

	@Column(name = "quantity")
	private int quantity;

	@Column(name = "amount")
	private double amount;

	@Column(name = "weight")
	private double weight;

	@Column(name = "options")
	private String options;

	@Deprecated
	HibernateOrderItem() {
	}

	public HibernateOrderItem(HibernateOrder order, BasketItem item, int number) {
		this.pk = new Pk(order.getId(), number);

		final ProductPreview product = item.getProduct();

		this.product = product;
		this.quantity = item.getQuantity();

		this.weight = product.getWeight();
		this.amount = product.getPrice().getAmount();

		StringBuilder sb = new StringBuilder();
		final Collection<Property> options1 = item.getOptions();
		if (options1 != null) {
			for (Property property : options1) {
				sb.append(property.getAttribute().getName()).append(": ").append(property.getValue());
				sb.append("; ");
			}
			if (sb.length() > 2) {
				sb.setLength(sb.length() - 2);
			}
		}
		this.options = sb.toString();
	}

	public Integer getNumber() {
		return pk.number;
	}

	@Override
	public ProductPreview getProduct() {
		return product;
	}

	@Override
	public int getQuantity() {
		return quantity;
	}

	@Override
	public double getAmount() {
		return amount;
	}

	@Override
	public double getWeight() {
		return weight;
	}

	@Override
	public String getOptions() {
		return options;
	}

	@Embeddable
	public static class Pk implements Serializable {
		@Column(name = "orderId")
		private Long orderId;

		@Column(name = "number")
		private Integer number;

		public Pk() {
		}

		public Pk(Long orderId, Integer number) {
			this.number = number;
			this.orderId = orderId;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Pk pk = (Pk) o;

			if (number != null ? !number.equals(pk.number) : pk.number != null) return false;
			if (orderId != null ? !orderId.equals(pk.orderId) : pk.orderId != null) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = orderId != null ? orderId.hashCode() : 0;
			result = 31 * result + (number != null ? number.hashCode() : 0);
			return result;
		}
	}
}
