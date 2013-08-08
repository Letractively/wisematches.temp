package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Article extends ArticleDescription {
	/**
	 * Returns full description for this article.
	 *
	 * @return the full description for this article.
	 */
	String getDescription();

	/**
	 * Returns available options for this article which customer can choice.
	 *
	 * @return available options for this article which customer can choice or {@code null} if there is no one.
	 */
	List<Option> getOptions();

	/**
	 * Returns ids for all available images for this article.
	 *
	 * @return the ids for all available images for this article.
	 */
	List<String> getImageIds();

	/**
	 * Returns reference to supplier
	 *
	 * @return the reference to supplier
	 */
	SupplierInfo getSupplierInfo();

	/**
	 * Returns characters for this article.
	 *
	 * @return the characters for this article.
	 */
	List<Property> getProperties();

	/**
	 * Get accessories for this articles or {@code null} ir there is no one.
	 *
	 * @return accessories for this articles or {@code null} ir there is no one.
	 */
	List<ArticleDescription> getAccessories();
}