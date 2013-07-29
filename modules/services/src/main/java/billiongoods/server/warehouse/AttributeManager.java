package billiongoods.server.warehouse;

import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface AttributeManager {
    Attribute getAttribute(Integer id);

    Collection<Attribute> getAttributes();


    Attribute addAttribute(String name, String unit);

    Attribute removeAttribute(Integer id);
}
