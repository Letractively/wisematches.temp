package billiongoods.server.warehouse;

import java.math.BigDecimal;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public enum AttributeType {
	UNKNOWN() {
		@Override
		public String convert(String s) {
			return s;
		}
	},

	STRING() {
		@Override
		public String convert(String s) {
			return s;
		}
	},
	INTEGER() {
		@Override
		public BigDecimal convert(String s) {
			return new BigDecimal(s);
		}
	},
	BOOLEAN() {
		@Override
		public Boolean convert(String s) {
			return Boolean.valueOf(s);
		}
	};

	public abstract Object convert(String s);
}
