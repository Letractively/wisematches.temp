package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class Property {
	private final Attribute attribute;
	private final Object value;

	public Property(Attribute attribute, Object value) {
		this.attribute = attribute;
		this.value = value;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Property)) return false;

		Property property = (Property) o;

		if (!attribute.equals(property.attribute)) return false;
		if (value != null ? !value.equals(property.value) : property.value != null) return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = attribute.hashCode();
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Property{" +
				"attribute=" + attribute +
				", value='" + value + '\'' +
				'}';
	}
}
