package billiongoods.server.warehouse;

import billiongoods.core.search.SearchManager;

import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ArticleManager extends SearchManager<ArticleDescription, ArticleContext> {
    Article getArticle(Integer id);

    Article getArticle(String sku);

    ArticleDescription getDescription(Integer id);


    Article createArticle(String name, String description, Category category, Price price, double weight, Date restockDate,
                          String previewImage, List<String> imageIds, List<Option> options, List<Property> properties,
                          String referenceUri, String referenceCode, Supplier wholesaler, Price supplierPrice);

    Article updateArticle(Integer id, String name, String description, Category category, Price price, double weight, Date restockDate,
                          String previewImage, List<String> imageIds, List<Option> options, List<Property> properties,
                          String referenceUri, String referenceCode, Supplier wholesaler, Price supplierPrice);

    void updateSold(Integer id, int quantity);

    void updateState(Integer id, boolean active);

    void updatePrice(Integer id, Price price, Price supplierPrice);
}
