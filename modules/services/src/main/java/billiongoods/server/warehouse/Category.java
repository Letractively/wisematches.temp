package billiongoods.server.warehouse;

import java.util.List;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Category {
    Integer getId();


    int getLevel();

    String getName();

    String getDescription();


    boolean isFinal();

    boolean isActive();


    Category getParent();

    Genealogy getGenealogy();


    List<Category> getChildren();

    Set<Attribute> getAttributes();
}
