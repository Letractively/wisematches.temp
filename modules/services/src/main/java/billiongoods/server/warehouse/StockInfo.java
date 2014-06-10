package billiongoods.server.warehouse;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class StockInfo {
	@Column(name = "stockCount")
	private int count;

	@Column(name = "stockSoldCount")
	private int soldCount;

	@Column(name = "stockShipDays")
	private int shipDays;

	@Column(name = "stockArrivalDate")
	private LocalDate arrivalDate;

	private static final int DEFAULT_SHIP_DAYS = 3;

	public StockInfo() {
		this(0, 0, DEFAULT_SHIP_DAYS);
	}

	public StockInfo(int count, int soldCount, int shipDays) {
		this(count, soldCount, shipDays, null);
	}

	public StockInfo(int count, int soldCount, int shipDays, LocalDate arrivalDate) {
		this.count = count;
		this.soldCount = soldCount;
		this.shipDays = shipDays;
		this.arrivalDate = arrivalDate;
	}


	public int getCount() {
		return count;
	}

	public int getSoldCount() {
		return soldCount;
	}

	public int getShipDays() {
		return shipDays;
	}

	public LocalDate getArrivalDate() {
		return arrivalDate;
	}


	public StockState getStockState() {
		if ((count > 0) || (count == 0 && shipDays >= 9)) {
			return StockState.IN_STOCK;
		}
		return arrivalDate != null ? StockState.OUT_STOCK : StockState.SOLD_OUT;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof StockInfo)) return false;

		StockInfo stockInfo = (StockInfo) o;

		if (count != stockInfo.count) return false;
		if (shipDays != stockInfo.shipDays) return false;
		if (soldCount != stockInfo.soldCount) return false;
		if (arrivalDate != null ? !arrivalDate.equals(stockInfo.arrivalDate) : stockInfo.arrivalDate != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = count;
		result = 31 * result + soldCount;
		result = 31 * result + shipDays;
		result = 31 * result + (arrivalDate != null ? arrivalDate.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("StockInfo{");
		sb.append("count=").append(count);
		sb.append(", soldCount=").append(soldCount);
		sb.append(", shipDays=").append(shipDays);
		sb.append(", arrivalDate=").append(arrivalDate);
		sb.append('}');
		return sb.toString();
	}
}
