package billiongoods.server.warehouse;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface Article extends ArticleDescription {
    /**
     * Returns number of sold articles.
     *
     * @return number of sold articles.
     */
    int getSoldCount();

    /**
     * Returns full description for this article.
     *
     * @return the full description for this article.
     */
    String getDescription();

    /**
     * Returns ids for all available images for this article.
     *
     * @return the ids for all available images for this article.
     */
    List<String> getImageIds();

    /**
     * Returns available options for this article which customer can choice.
     *
     * @return available options for this article which customer can choice or {@code null} if there is no one.
     */
    List<Option> getOptions();

    /**
     * Get accessories for this articles or {@code null} ir there is no one.
     *
     * @return accessories for this articles or {@code null} ir there is no one.
     */
    List<Long> getAccessories();

    /**
     * Returns characters for this article.
     *
     * @return the characters for this article.
     */
    List<Property> getProperties();

    /**
     * Returns reference to supplier
     *
     * @return the reference to supplier
     */
    SupplierInfo getSupplierInfo();
}