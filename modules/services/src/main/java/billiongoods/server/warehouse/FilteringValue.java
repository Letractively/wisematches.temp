package billiongoods.server.warehouse;

import java.math.BigDecimal;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface FilteringValue {
	boolean isEmpty();

	boolean isAllowed(Object value);

	public static class Enum implements FilteringValue {
		private final Set<String> values;

		public Enum(Set<String> values) {
			this.values = values;
		}

		@Override
		public boolean isEmpty() {
			return values.isEmpty() || (values.size() == 1 && values.contains(""));
		}

		public Set<String> getValues() {
			return values;
		}

		@Override
		public boolean isAllowed(Object value) {
			return value instanceof String && (values == null || values.contains(value));
		}
	}

	public static class Bool implements FilteringValue {
		private Boolean value;

		public Bool(Boolean value) {
			this.value = value;
		}

		@Override
		public boolean isEmpty() {
			return value == null;
		}

		public Boolean getValue() {
			return value;
		}

		@Override
		public boolean isAllowed(Object value) {
			return value instanceof Boolean && (this.value == null || this.value.equals(value));
		}
	}

	public static class Range implements FilteringValue {
		private BigDecimal min;
		private BigDecimal max;

		public Range(BigDecimal min, BigDecimal max) {
			this.min = min;
			this.max = max;
		}

		@Override
		public boolean isEmpty() {
			return min == null && max == null;
		}

		public BigDecimal getMin() {
			return min;
		}

		public BigDecimal getMax() {
			return max;
		}

		@Override
		public boolean isAllowed(Object value) {
			if (!(value instanceof BigDecimal)) {
				return false;
			}

			final BigDecimal v = (BigDecimal) value;
			if (min != null && min.compareTo(v) > 0) {
				return false;
			}
			if (max != null && max.compareTo(v) < 0) {
				return false;
			}
			return true;

		}
	}
}
