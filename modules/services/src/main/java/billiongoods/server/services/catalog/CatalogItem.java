package billiongoods.server.services.catalog;

import billiongoods.server.services.catalog.impl.HibernateCatalogItem;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface CatalogItem {
    int getId();

    int getLevel();

    String getName();


    boolean isFinal();


    CatalogItem getParent();

    List<HibernateCatalogItem> getCatalogItems();
}
