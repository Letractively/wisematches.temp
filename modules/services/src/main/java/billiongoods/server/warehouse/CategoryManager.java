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
	 * @param editor category editor
	 * @return create catalog item.
	 * @throws NullPointerException     if name or parent is null
	 * @throws IllegalArgumentException if parent catalog already has item with the same name
	 */
	Category createCategory(Category.Editor editor);

	/**
	 * Updates settings of exist category
	 *
	 * @param editor category editor
	 * @return updated category
	 */
	Category updateCategory(Category.Editor editor);


	void addParameterValue(Category category, Attribute attribute, String value);

	void removeParameterValue(Category category, Attribute attribute, String value);
}
