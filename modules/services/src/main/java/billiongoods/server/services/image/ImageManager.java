package billiongoods.server.services.image;

import billiongoods.server.warehouse.ArticleDescription;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * {@code ImageManager} allows get and set image for players.
 *
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public interface ImageManager {
	void addImage(ArticleDescription article, String code, InputStream in) throws IOException;

	void removeImage(ArticleDescription article, String code) throws IOException;


	Collection<String> getImageCodes(ArticleDescription article) throws IOException;
}