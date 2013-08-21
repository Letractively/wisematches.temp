package billiongoods.server.warehouse;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface SupplierInfo {
	/**
	 * Returns supplier price
	 *
	 * @return the supplier price
	 */
	Price getPrice();

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
