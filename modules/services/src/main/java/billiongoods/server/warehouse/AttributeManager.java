package billiongoods.server.warehouse;

import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface AttributeManager {
	Attribute getAttribute(Integer id);


	Attribute createAttribute(String name, String unit);

	Attribute updateAttribute(Integer id, String name, String unit);


	Collection<Attribute> getAttributes();

	Collection<Attribute> getAttributes(String name);
}
