package billiongoods.server.warehouse;

import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class Parameter {
	private final Attribute attribute;
	private final Set<String> values;

	public Parameter(Attribute attribute, Set<String> values) {
		this.attribute = attribute;
		this.values = values;
	}

	public Attribute getAttribute() {
		return attribute;
	}

	public Set<String> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return "Parameter{" +
				"attribute=" + attribute +
				", values=" + values +
				'}';
	}
}
