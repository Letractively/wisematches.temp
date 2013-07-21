package billiongoods.server.warehouse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface CategoryManager {
    /**
     * Returns root element of the catalog.
     *
     * @return the root element of the catalog.
     */
    Category getCatalog();

    /**
     * Adds new catalog with specified to specified parent catalog.
     *
     * @param name   the name of new catalog item.
     * @param parent parent catalog;
     * @return create catalog item.
     * @throws NullPointerException     if name or parent is null
     * @throws IllegalArgumentException if parent catalog already has item with the same name
     */
    Category addCatalogItem(String name, Category parent);

    /**
     * Removes specified catalog item and moves all it's commodities to specified parent.
     *
     * @param item      the catalog item to be removed.
     * @param newParent new parent there all commodities will be moved to.
     * @return removed catalog item.
     * @throws NullPointerException if item or newParent is null
     */
    Category removeCatalogItem(Category item, Category newParent);
}
