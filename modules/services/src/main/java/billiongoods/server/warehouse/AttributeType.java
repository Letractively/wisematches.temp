package billiongoods.server.warehouse;

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
		public Integer convert(String s) {
			return new Integer(s);
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
