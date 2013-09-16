package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class Option {
	private final StoreAttribute attribute;
	private final List<String> values;

	public Option(StoreAttribute attribute, List<String> values) {
		this.attribute = attribute;
		this.values = values;
	}

	public StoreAttribute getAttribute() {
		return attribute;
	}

	public List<String> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return "Option{" +
				"attribute=" + attribute +
				", values=" + values +
				'}';
	}
}
