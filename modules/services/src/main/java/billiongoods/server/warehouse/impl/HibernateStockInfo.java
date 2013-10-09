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

	@Column(name = "stockLeftovers")
	private Integer leftovers;

	@Column(name = "stockRestockDate")
	@Temporal(TemporalType.DATE)
	private Date restockDate;

	HibernateStockInfo() {
	}

	public HibernateStockInfo(StockInfo stockInfo) {
		this(stockInfo != null ? stockInfo.getLeftovers() : null,
				stockInfo != null ? stockInfo.getRestockDate() : null);
	}

	public HibernateStockInfo(Integer leftovers, Date restockDate) {
		this.leftovers = leftovers;
		this.restockDate = restockDate;
	}

	@Override
	public Integer getLeftovers() {
		return leftovers;
	}

	@Override
	public StockState getStockState() {
		if (leftovers != null) {
			if (leftovers == 0) {
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
		this.leftovers = available;
		this.restockDate = restockDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof HibernateStockInfo)) return false;

		HibernateStockInfo that = (HibernateStockInfo) o;

		if (leftovers != null ? !leftovers.equals(that.leftovers) : that.leftovers != null) return false;
		if (restockDate != null ? !restockDate.equals(that.restockDate) : that.restockDate != null) return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = leftovers != null ? leftovers.hashCode() : 0;
		result = 31 * result + (restockDate != null ? restockDate.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateStockInfo{");
		sb.append("leftovers=").append(leftovers);
		sb.append(", restockDate=").append(restockDate);
		sb.append('}');
		return sb.toString();
	}
}
