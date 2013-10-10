package billiongoods.server.services.validator.impl;

import billiongoods.server.services.validator.ProductValidation;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.StockInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "service_validation")
public class HibernateProductValidation implements ProductValidation {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "productId")
	private Integer productId;

	@Column(name = "timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@Column(name = "errMsg")
	private String errorMessage;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "amount", column = @Column(name = "op")),
			@AttributeOverride(name = "primordialAmount", column = @Column(name = "opp"))
	})
	private Price oldPrice;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "amount", column = @Column(name = "osp")),
			@AttributeOverride(name = "primordialAmount", column = @Column(name = "ospp"))
	})
	private Price oldSupplierPrice;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "amount", column = @Column(name = "np")),
			@AttributeOverride(name = "primordialAmount", column = @Column(name = "npp"))
	})
	private Price newPrice;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "amount", column = @Column(name = "nsp")),
			@AttributeOverride(name = "primordialAmount", column = @Column(name = "nspp"))
	})
	private Price newSupplierPrice;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "leftovers", column = @Column(name = "oa")),
			@AttributeOverride(name = "restockDate", column = @Column(name = "ord"))
	})
	private StockInfo oldStockInfo;

	@Embedded
	@AttributeOverrides({
			@AttributeOverride(name = "leftovers", column = @Column(name = "na")),
			@AttributeOverride(name = "restockDate", column = @Column(name = "nrd"))
	})
	private StockInfo newStockInfo;

	@Deprecated
	HibernateProductValidation() {
	}

	public HibernateProductValidation(Integer productId, Date timestamp, Price oldPrice, Price oldSupplierPrice, StockInfo oldStockInfo) {
		this.productId = productId;
		this.timestamp = timestamp;
		this.oldPrice = oldPrice;
		this.oldSupplierPrice = oldSupplierPrice;
		this.oldStockInfo = oldStockInfo;
	}

	@Override
	public Integer getProductId() {
		return productId;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public Price getOldPrice() {
		return oldPrice;
	}

	@Override
	public Price getOldSupplierPrice() {
		return oldSupplierPrice;
	}

	@Override
	public Price getNewPrice() {
		return newPrice;
	}

	@Override
	public Price getNewSupplierPrice() {
		return newSupplierPrice;
	}

	@Override
	public StockInfo getOldStockInfo() {
		return oldStockInfo;
	}

	@Override
	public StockInfo getNewStockInfo() {
		return newStockInfo;
	}

	@Override
	public boolean hasChanges() {
		if (newPrice != null && !newPrice.equals(oldPrice)) {
			return true;
		}
		if (newSupplierPrice != null && !newSupplierPrice.equals(oldSupplierPrice)) {
			return true;
		}
		if (newStockInfo != null && !newStockInfo.equals(oldStockInfo)) {
			return true;
		}
		return false;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	void priceValidated(Price newPrice, Price newSupplierPrice) {
		this.newPrice = newPrice;
		this.newSupplierPrice = newSupplierPrice;
	}

	void stockValidated(StockInfo newStockInfo) {
		this.newStockInfo = newStockInfo;
	}

	void processingError(Exception ex) {
		this.errorMessage = ex.getMessage();
		if (this.errorMessage.length() > 250) {
			this.errorMessage = this.errorMessage.substring(0, 250);
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateProductValidation{");
		sb.append("id=").append(id);
		sb.append(", productId=").append(productId);
		sb.append(", timestamp=").append(timestamp);
		sb.append(", errorMessage='").append(errorMessage).append('\'');
		sb.append(", oldPrice=").append(oldPrice);
		sb.append(", oldSupplierPrice=").append(oldSupplierPrice);
		sb.append(", newPrice=").append(newPrice);
		sb.append(", newSupplierPrice=").append(newSupplierPrice);
		sb.append(", oldStockInfo=").append(oldStockInfo);
		sb.append(", newStockInfo=").append(newStockInfo);
		sb.append('}');
		return sb.toString();
	}
}