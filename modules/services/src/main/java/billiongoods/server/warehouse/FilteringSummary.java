package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class FilteringSummary {
	private final String name;
	private final int count;

	public FilteringSummary(String name, int count) {
		this.name = name;
		this.count = count;
	}

	public String getName() {
		return name;
	}

	public int getCount() {
		return count;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("FilteringSummary{");
		sb.append("name='").append(name).append('\'');
		sb.append(", count=").append(count);
		sb.append('}');
		return sb.toString();
	}
}
