package billiongoods.server.warehouse;

import billiongoods.core.search.SearchManager;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface AttributeManager extends SearchManager<Attribute, Void> {
    Attribute getAttribute(Integer id);

    Attribute addAttribute(String name, String unit);

    void removeAttribute(Attribute attribute);
}
