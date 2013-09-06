package billiongoods.server.warehouse;

import java.net.URL;
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
	 * Returns absolute HTTP URL for original product.
	 *
	 * @return the absolute HTTP URL for original product.
	 */
	URL getReferenceUrl();

	/**
	 * Returns link to wholesaler WEB page for this product.
	 *
	 * @return the link to wholesaler WEB page for this product.
	 */
	String getReferenceUri();

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
	 * Returns date when the product was checked last time. Never null.
	 *
	 * @return the date when the product was checked last time.
	 */
	Date getValidationDate();
}
