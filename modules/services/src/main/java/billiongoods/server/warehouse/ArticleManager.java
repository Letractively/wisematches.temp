package billiongoods.server.warehouse;

import billiongoods.core.search.SearchManager;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ArticleManager extends SearchManager<ArticleDescription, ArticleContext> {
	Article getArticle(Long id);
}
