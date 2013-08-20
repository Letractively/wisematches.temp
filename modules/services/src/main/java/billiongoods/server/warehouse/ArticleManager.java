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


	Article createArticle(String name, String description, Category category,
						  double price, Double primordialPrice, double weight, Date restockDate,
						  String previewImage, List<String> imageIds, List<Option> options, List<Property> properties,
						  String referenceId, String referenceCode, Supplier wholesaler,
						  double supplierPrice, Double supplierPrimordialPrice);

	Article updateArticle(Integer id, String name, String description, Category category,
						  double price, Double primordialPrice, double weight, Date restockDate,
						  String previewImage, List<String> imageIds, List<Option> options, List<Property> properties,
						  String referenceId, String referenceCode, Supplier wholesaler,
						  double supplierPrice, Double supplierPrimordialPrice);

	void updateSold(Integer id, int quantity);

	void updateState(Integer id, boolean active);

	void updatePrice(Integer id, double price, Double primordialPrice, double supplierPrice, Double supplierPrimordialPrice);
}
