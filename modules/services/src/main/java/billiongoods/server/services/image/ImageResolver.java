package billiongoods.server.services.image;

import billiongoods.server.warehouse.ArticleDescription;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ImageResolver {
	String resolveImagePath(ArticleDescription article, String id, ImageType type);
}
