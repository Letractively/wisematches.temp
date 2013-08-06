package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class Property {
	private final Attribute attribute;
	private final String value;

	public Property(Attribute attribute, String value) {
		this.attribute = attribute;
		this.value = value;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Property{" +
				"attribute=" + attribute +
				", value='" + value + '\'' +
				'}';
	}
}
