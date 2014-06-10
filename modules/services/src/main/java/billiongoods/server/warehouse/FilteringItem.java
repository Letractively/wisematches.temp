package billiongoods.server.warehouse;

import java.util.Map;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class FilteringItem {
	private final Attribute attribute;

	protected FilteringItem(Attribute attribute) {
		this.attribute = attribute;
	}

	public abstract boolean isEmpty();

	public Attribute getAttribute() {
		return attribute;
	}

	public static class Enum extends FilteringItem {
		private final Map<?, Integer> valuesCount;

		public Enum(Attribute attribute, Map<?, Integer> valuesCount) {
			super(attribute);
			this.valuesCount = valuesCount;
		}

		@Override
		public boolean isEmpty() {
			return valuesCount.size() <= 1;
		}

		public Set<?> getValues() {
			return valuesCount.keySet();
		}

		public int getValueCount(Object value) {
			final Integer integer = valuesCount.get(value);
			if (integer == null) {
				return 0;
			}
			return integer;
		}
	}

	public static class Range extends FilteringItem {
		private final Integer min;
		private final Integer max;

		public Range(Attribute attribute, Integer min, Integer max) {
			super(attribute);
			this.min = min;
			this.max = max;
		}

		@Override
		public boolean isEmpty() {
			return min == null || max == null;
		}

		public Integer getMin() {
			return min;
		}

		public Integer getMax() {
			return max;
		}
	}
}