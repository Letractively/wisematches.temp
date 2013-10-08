package billiongoods.server.services.supplier;

import billiongoods.server.warehouse.Price;

import java.util.Collection;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface SupplierDescription {
	Price getPrice();

	Availability getAvailability();

	public Map<String, Collection<String>> getParameters();
}
