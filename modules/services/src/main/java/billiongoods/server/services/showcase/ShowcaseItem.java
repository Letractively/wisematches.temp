package billiongoods.server.services.showcase;

import billiongoods.server.warehouse.ArticleContext;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ShowcaseItem {
	String getName();

	String getMoreInfoUri();

	ArticleContext getArticleContext();
}
