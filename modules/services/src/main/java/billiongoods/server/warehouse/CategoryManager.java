package billiongoods.server.warehouse;

import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface CategoryManager {
	/**
	 * Returns root element of the catalog.
	 *
	 * @return the root element of the catalog.
	 */
	Catalog getCatalog();

	/**
	 * Returns category by it's id. If there is no category with specified id {@code null} will be returned.
	 *
	 * @param id the if of required category.
	 * @return the category by specified id or {@code null} if there is no one.
	 */
	Category getCategory(Integer id);


	/**
	 * Adds new catalog with specified to specified parent catalog.
	 *
	 * @param name     the name of new catalog item.
	 * @param parent   parent catalog;
	 * @param position
	 * @return create catalog item.
	 * @throws NullPointerException     if name or parent is null
	 * @throws IllegalArgumentException if parent catalog already has item with the same name
	 */
	Category createCategory(String name, String description, Set<Attribute> attributes, Category parent, int position);

	/**
	 * Updates settings of exist category
	 *
	 * @param id          the category id
	 * @param name        new name
	 * @param description new description
	 * @param attributes  new attributes set
	 * @param parent      new parent
	 * @param position
	 * @return updated category
	 */
	Category updateCategory(Integer id, String name, String description, Set<Attribute> attributes, Category parent, int position);
}
