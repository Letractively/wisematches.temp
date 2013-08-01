package billiongoods.server.services.image;

import billiongoods.server.warehouse.ArticleDescription;

import java.nio.file.Path;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ImageResolver {
	Path resolvePath(ArticleDescription article);

	Path resolveFile(ArticleDescription article, String code, ImageSize size);


	String resolveURI(ArticleDescription article, String code, ImageSize size);
}
