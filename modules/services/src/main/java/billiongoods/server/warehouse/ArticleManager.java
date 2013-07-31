package billiongoods.server.warehouse;

import billiongoods.core.search.SearchManager;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ArticleManager extends SearchManager<ArticleDescription, ArticleContext> {
	Article getArticle(Long id);

	Article createArticle(String name, String description, Category category,
						  float price, Float primordialPrice, Date restockDate,
						  String referenceId, String referenceCode,
						  Supplier wholesaler, float supplierPrice, Float supplierPrimordialPrice);

	Article updateArticle(Long id, String name, String description);
}
