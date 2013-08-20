package billiongoods.server.warehouse.impl;

import billiongoods.server.services.price.MarkupCalculator;
import billiongoods.server.warehouse.SupplierInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "service_price_validation")
public class HibernatePriceValidation {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private Integer articleId;

	private Date timestamp;

	private double oldPrice;

	private Double oldPrimordialPrice;

	private double oldSupplierPrice;

	private Double oldSupplierPrimordialPrice;

	private double newPrice;

	private Double newPrimordialPrice;

	private double newSupplierPrice;

	private Double newSupplierPrimordialPrice;

	public HibernatePriceValidation(Integer articleId, double oldPrice, Double oldPrimordialPrice, SupplierInfo info) {
		this.articleId = articleId;
		this.oldPrice = oldPrice;
		this.oldPrimordialPrice = oldPrimordialPrice;
		this.oldSupplierPrice = info.getPrice();
		this.oldSupplierPrimordialPrice = info.getPrimordialPrice();
		this.timestamp = new Date();
	}

	public Integer getArticleId() {
		return articleId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public double getOldPrice() {
		return oldPrice;
	}

	public Double getOldPrimordialPrice() {
		return oldPrimordialPrice;
	}

	public double getOldSupplierPrice() {
		return oldSupplierPrice;
	}

	public Double getOldSupplierPrimordialPrice() {
		return oldSupplierPrimordialPrice;
	}

	public double getNewPrice() {
		return newPrice;
	}

	public Double getNewPrimordialPrice() {
		return newPrimordialPrice;
	}

	public double getNewSupplierPrice() {
		return newSupplierPrice;
	}

	public Double getNewSupplierPrimordialPrice() {
		return newSupplierPrimordialPrice;
	}

	public boolean validate(MarkupCalculator calculator, double newSupplierPrice, Double newSupplierPrimordialPrice) {
		this.newPrice = calculator.calculateFinalPrice(newSupplierPrice);
		this.newPrimordialPrice = calculator.calculateFinalPrice(newSupplierPrimordialPrice);
		this.newSupplierPrice = newSupplierPrice;
		this.newSupplierPrimordialPrice = newSupplierPrimordialPrice;

		return Double.compare(oldSupplierPrice, newSupplierPrice) != 0 ||
				(oldSupplierPrimordialPrice == null && newSupplierPrimordialPrice != null) ||
				(newSupplierPrimordialPrice == null && oldSupplierPrimordialPrice != null) ||
				(oldSupplierPrimordialPrice != null && !oldSupplierPrimordialPrice.equals(newSupplierPrimordialPrice));
	}


	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("PriceValidation{");
		sb.append("articleId=").append(articleId);
		sb.append(", price: ").append(oldPrice).append("->").append(newPrice);
		sb.append(", primordialPrice: ").append(oldPrimordialPrice).append("->").append(newPrimordialPrice);
		sb.append(", supplierPrice: ").append(oldSupplierPrice).append("->").append(newSupplierPrice);
		sb.append(", supplierPrimordialPrice: ").append(oldSupplierPrimordialPrice).append("->").append(newSupplierPrimordialPrice);
		sb.append('}');
		return sb.toString();
	}
}
