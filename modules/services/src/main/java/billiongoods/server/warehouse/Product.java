package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Product extends ProductPreview {
	/**
	 * Returns full description for this product.
	 *
	 * @return the full description for this product.
	 */
	String getDescription();

	/**
	 * Returns available options for this product which customer can choice.
	 *
	 * @return available options for this product which customer can choice or {@code null} if there is no one.
	 */
	List<Option> getOptions();

	/**
	 * Returns ids for all available images for this product.
	 *
	 * @return the ids for all available images for this product.
	 */
	List<String> getImageIds();

	/**
	 * Returns characters for this product.
	 *
	 * @return the characters for this product.
	 */
	List<Property> getProperties();
}