package billiongoods.server.warehouse;

import billiongoods.server.warehouse.impl.HibernateCategory;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Category {
    int getId();

    int getLevel();

    String getName();


    boolean isFinal();


    Category getParent();

    List<HibernateCategory> getCatalogItems();
}
