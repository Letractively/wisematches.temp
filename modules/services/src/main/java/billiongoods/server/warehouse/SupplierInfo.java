package billiongoods.server.warehouse;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface SupplierInfo {
    /**
     * Returns current supplier's price. If there is any discount from supplier,
     * this value contains discounted price.
     *
     * @return the current supplier's price.
     */
    double getPrice();

    /**
     * Returns primordial supplier's price. This value can be used
     * to get supplier discount and is {@code null} if there is no discount. Returned value
     * contains original, primordial price before discount.
     *
     * @return the primordial supplier's price or {@code null} if there is no any discount.
     */
    Float getPrimordialPrice();

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
