package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class FilteringSummary {
	private final String name;
	private final Boolean value;
	private final int count;
	private final Integer minValue;
	private final Integer maxValue;

	public FilteringSummary(String name, int count) {
		this(name, null, count, 0, 0);
	}

	public FilteringSummary(Boolean value, int count) {
		this(null, value, count, 0, 0);
	}

	public FilteringSummary(Integer minValue, Integer maxValue) {
		this(null, null, 0, minValue, maxValue);
	}

	private FilteringSummary(String name, Boolean value, int count, Integer minValue, Integer maxValue) {
		this.name = name;
		this.value = value;
		this.count = count;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public String getName() {
		return name;
	}

	public Boolean getValue() {
		return value;
	}

	public int getCount() {
		return count;
	}

	public Integer getMinValue() {
		return minValue;
	}

	public Integer getMaxValue() {
		return maxValue;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("FilteringSummary{");
		sb.append("name='").append(name).append('\'');
		sb.append(", count=").append(count);
		sb.append(", minValue=").append(minValue);
		sb.append(", maxValue=").append(maxValue);
		sb.append('}');
		return sb.toString();
	}
}
