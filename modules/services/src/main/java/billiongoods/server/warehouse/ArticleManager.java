package billiongoods.server.warehouse;

import billiongoods.core.search.SearchManager;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ArticleManager extends SearchManager<ArticleDescription, ArticleContext> {
	void addArticleListener(ArticleListener l);

	void removeArticleListener(ArticleListener l);

	Article getArticle(Integer id);

	Article getArticle(String sku);

	ArticleDescription getDescription(Integer id);


	Article createArticle(ArticleEditor editor);

	Article updateArticle(Integer id, ArticleEditor editor);

	Article removeArticle(Integer id);


	void updateSold(Integer id, int quantity);

	void updatePrice(Integer id, Price price, Price supplierPrice);
}
