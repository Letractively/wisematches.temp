package billiongoods.server.warehouse;

import billiongoods.core.search.SearchManager;

import java.util.Date;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ArticleManager extends SearchManager<ArticleDescription, ArticleContext> {
	Article getArticle(Integer id);

	ArticleDescription getDescription(Integer id);

	Article createArticle(String name, String description, Category category,
						  float price, Float primordialPrice, Date restockDate,
						  String previewImage, List<String> imageIds, List<ArticleDescription> accessories,
						  List<Option> options, List<Property> properties,
						  String referenceId, String referenceCode, Supplier wholesaler,
						  float supplierPrice, Float supplierPrimordialPrice);

	Article updateArticle(Integer id, String name, String description, Category category,
						  float price, Float primordialPrice, Date restockDate,
						  String previewImage, List<String> imageIds, List<ArticleDescription> accessories,
						  List<Option> options, List<Property> properties,
						  String referenceId, String referenceCode, Supplier wholesaler,
						  float supplierPrice, Float supplierPrimordialPrice);
}
