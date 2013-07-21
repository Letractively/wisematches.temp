package billiongoods.server.warehouse;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ArticleReference {

    /**
     * Returns link to wholesaler WEB page for this article.
     *
     * @return the link to wholesaler WEB page for this article.
     */
    String getReference();

    /**
     * Returns name of wholesaler.
     *
     * @return the name of wholesaler.
     */
    Supplier getWholesaler();


    float getBuyPrice();

    float getBuyDiscount();  // Float.NaN


    /**
     * Returns date when the article was checked last time. Never null.
     *
     * @return the date when the article was checked last time.
     */
    Date getValidationDate();
}
