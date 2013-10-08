package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.StockInfo;
import billiongoods.server.warehouse.StockState;
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
public class HibernateStockInfo implements StockInfo {
	@Formula("0")
	private byte dummy; // or stockInfo is null: https://issues.jboss.org/browse/HIBERNATE-50

	@Column(name = "stockAvailable")
	private Integer available;

	@Column(name = "restockDate")
	@Temporal(TemporalType.DATE)
	private Date restockDate;

	HibernateStockInfo() {
	}

	public HibernateStockInfo(StockInfo stockInfo) {
		this(stockInfo != null ? stockInfo.getAvailable() : null,
				stockInfo != null ? stockInfo.getRestockDate() : null);
	}

	public HibernateStockInfo(Integer available, Date restockDate) {
		this.available = available;
		this.restockDate = restockDate;
	}

	@Override
	public Integer getAvailable() {
		return available;
	}

	@Override
	public StockState getStockState() {
		if (available != null) {
			if (available == 0) {
				return StockState.SOLD_OUT;
			} else {
				return StockState.LIMITED_NUMBER;
			}
		}
		if (restockDate != null) {
			return StockState.OUT_STOCK;
		}
		return StockState.IN_STOCK;
	}

	@Override
	public Date getRestockDate() {
		return restockDate;
	}

	void setRestockInfo(Integer available, Date restockDate) {
		this.available = available;
		this.restockDate = restockDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof HibernateStockInfo)) return false;

		HibernateStockInfo that = (HibernateStockInfo) o;

		if (available != null ? !available.equals(that.available) : that.available != null) return false;
		if (restockDate != null ? !restockDate.equals(that.restockDate) : that.restockDate != null) return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = available != null ? available.hashCode() : 0;
		result = 31 * result + (restockDate != null ? restockDate.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateStockInfo{");
		sb.append("available=").append(available);
		sb.append(", restockDate=").append(restockDate);
		sb.append('}');
		return sb.toString();
	}
}
