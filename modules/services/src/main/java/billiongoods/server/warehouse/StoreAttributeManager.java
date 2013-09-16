package billiongoods.server.warehouse;

import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Deprecated
public interface StoreAttributeManager {
	StoreAttribute getAttribute(Integer id);


	StoreAttribute createAttribute(String name, String unit);

	StoreAttribute updateAttribute(Integer id, String name, String unit);


	Collection<StoreAttribute> getAttributes();

	Collection<StoreAttribute> getAttributes(String name);
}
