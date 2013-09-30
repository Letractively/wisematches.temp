package billiongoods.server.warehouse;

import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface AttributeManager {
	Attribute getAttribute(Integer id);


	Attribute createAttribute(Attribute.Editor editor);

	Attribute updateAttribute(Attribute.Editor editor);


	Collection<Attribute> getAttributes();

	Collection<Attribute> getAttributes(String name);
}
