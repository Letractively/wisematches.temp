package billiongoods.server.warehouse;

import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class StockInfo {
	@Formula("0")
	private byte dummy; // or stockInfo is null: https://issues.jboss.org/browse/HIBERNATE-50

	@Column(name = "stockDelivery")
	private int deliveryDays;

	@Column(name = "stockLeftovers")
	private Integer leftovers;

	@Column(name = "stockRestockDate")
	@Temporal(TemporalType.DATE)
	private Date restockDate;

	private static final int DEFAULT_DELIVERY_DATES = 3;

	public StockInfo() {
		this(DEFAULT_DELIVERY_DATES);
	}

	public StockInfo(int deliveryDays) {
		this(deliveryDays, null, null);
	}

	public StockInfo(StockInfo stockInfo) {
		this(stockInfo != null ? stockInfo.getDeliveryDays() : DEFAULT_DELIVERY_DATES,
				stockInfo != null ? stockInfo.getLeftovers() : null,
				stockInfo != null ? stockInfo.getRestockDate() : null);
	}

	public StockInfo(Integer leftovers, Date restockDate) {
		this(DEFAULT_DELIVERY_DATES, leftovers, restockDate);
	}

	public StockInfo(int deliveryDays, Integer leftovers, Date restockDate) {
		this.deliveryDays = deliveryDays;
		this.leftovers = leftovers;
		this.restockDate = restockDate;
	}

	public int getDeliveryDays() {
		return deliveryDays;
	}

	public Integer getLeftovers() {
		return leftovers;
	}

	public StockState getStockState() {
		if (restockDate != null) { // Have restock date? OUT_STOCK
			return StockState.OUT_STOCK;
		}
		if (leftovers != null) { // Have number?
			if (leftovers <= 0) { // it's less when zero - SOLD_OUT
				return StockState.SOLD_OUT;
			} else if (leftovers < 12) { // Less than 12 - limited number
				return StockState.LIMITED_NUMBER;
			}
		}
		return StockState.IN_STOCK;
	}

	public Date getRestockDate() {
		return restockDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StockInfo)) return false;

		StockInfo stockInfo = (StockInfo) o;

		if (deliveryDays != stockInfo.deliveryDays) return false;
		if (leftovers != null ? !leftovers.equals(stockInfo.leftovers) : stockInfo.leftovers != null) return false;
		if (restockDate != null ? !restockDate.equals(stockInfo.restockDate) : stockInfo.restockDate != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = deliveryDays;
		result = 31 * result + (leftovers != null ? leftovers.hashCode() : 0);
		result = 31 * result + (restockDate != null ? restockDate.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("StockInfo{");
		sb.append("deliveryDays=").append(deliveryDays);
		sb.append(", leftovers=").append(leftovers);
		sb.append(", restockDate=").append(restockDate);
		sb.append('}');
		return sb.toString();
	}
}
