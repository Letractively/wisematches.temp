package billiongoods.server.services.supplier;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class Availability {
	private final Date restockDate;
	private final Integer available;
	private final AvailabilityType type;

	public Availability(boolean inStock) {
		this(inStock ? AvailabilityType.IN_STOCK : AvailabilityType.SOLD_OUT, null, null);
	}

	public Availability(Integer available) {
		this(AvailabilityType.LIMITED_NUMBER, null, available);
	}

	public Availability(Date restockDate) {
		this(AvailabilityType.OUT_STOCK, restockDate, null);
	}

	private Availability(AvailabilityType type, Date restockDate, Integer available) {
		this.type = type;
		this.restockDate = restockDate;
		this.available = available;
	}

	public Integer getAvailable() {
		return available;
	}

	public Date getRestockDate() {
		return restockDate;
	}

	public AvailabilityType getType() {
		return type;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Availability{");
		sb.append("restockDate=").append(restockDate);
		sb.append(", available=").append(available);
		sb.append(", type=").append(type);
		sb.append('}');
		return sb.toString();
	}
}
