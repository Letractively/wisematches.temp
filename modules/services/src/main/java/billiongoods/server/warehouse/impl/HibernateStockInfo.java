package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.StockInfo;

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
	@Column(name = "stockSold")
	private int sold;

	@Column(name = "stockAvailable")
	private Integer available;

	@Column(name = "restockDate")
	@Temporal(TemporalType.DATE)
	private Date restockDate;

	HibernateStockInfo() {
	}

	public HibernateStockInfo(int sold, Integer available, Date restockDate) {
		this.sold = sold;
		this.available = available;
		this.restockDate = restockDate;
	}

	@Override
	public int getSold() {
		return sold;
	}

	@Override
	public Integer getRest() {
		return available;
	}

	@Override
	public boolean isLimited() {
		return available != null;
	}

	@Override
	public boolean isAvailable() {
		return restockDate == null;
	}

	@Override
	public Date getRestockDate() {
		return restockDate;
	}

	void setRestockInfo(Integer available, Date restockDate) {
		this.available = available;
		this.restockDate = restockDate;
	}
}
