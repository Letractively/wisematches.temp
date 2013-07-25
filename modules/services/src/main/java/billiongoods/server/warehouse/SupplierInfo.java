package billiongoods.server.warehouse;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface SupplierInfo {
	/**
	 * Returns original price by selling shop
	 *
	 * @return the original price by selling shop
	 */
	float getBuyPrice();

	/**
	 * Returns discount price by selling shop or {@link Float#NaN} if there is no discount.
	 *
	 * @return the discount price or {@link Float#NaN} if there is no discount.
	 */
	float getBuyDiscount();

	/**
	 * Returns link to wholesaler WEB page for this article.
	 *
	 * @return the link to wholesaler WEB page for this article.
	 */
	String getReferenceId();

	/**
	 * The reference code. It can differ from reference id.
	 *
	 * @return The reference code.
	 */
	String getReferenceCode();

	/**
	 * Returns name of wholesaler.
	 *
	 * @return the name of wholesaler.
	 */
	Supplier getWholesaler();

	/**
	 * Returns date when the article was checked last time. Never null.
	 *
	 * @return the date when the article was checked last time.
	 */
	Date getValidationDate();
}
