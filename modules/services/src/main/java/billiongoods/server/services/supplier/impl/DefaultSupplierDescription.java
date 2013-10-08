package billiongoods.server.services.supplier.impl;

import billiongoods.server.services.supplier.Availability;
import billiongoods.server.services.supplier.SupplierDescription;
import billiongoods.server.warehouse.Price;

import java.util.Collection;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class DefaultSupplierDescription implements SupplierDescription {
	private final Price price;
	private final Availability availability;
	private final Map<String, Collection<String>> parameters;

	public DefaultSupplierDescription(Price price, Availability availability, Map<String, Collection<String>> parameters) {
		this.price = price;
		this.availability = availability;
		this.parameters = parameters;
	}

	@Override
	public Price getPrice() {
		return price;
	}

	@Override
	public Availability getAvailability() {
		return availability;
	}

	@Override
	public Map<String, Collection<String>> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("DefaultSupplierDescription{");
		sb.append("price=").append(price);
		sb.append(", availability=").append(availability);
		sb.append(", parameters=").append(parameters);
		sb.append('}');
		return sb.toString();
	}
}
