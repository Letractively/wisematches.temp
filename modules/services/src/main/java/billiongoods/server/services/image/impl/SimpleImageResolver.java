package billiongoods.server.services.image.impl;

import billiongoods.server.services.image.ImageResolver;
import billiongoods.server.services.image.ImageType;
import billiongoods.server.warehouse.ArticleDescription;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class SimpleImageResolver implements ImageResolver {
	public SimpleImageResolver() {
	}

	@Override
	public String resolveImagePath(ArticleDescription article, String id, ImageType type) {
		return article.getCategory().getId() + "/" + article.getId() + "/" + type.getName() + "/" + id;
	}
}
