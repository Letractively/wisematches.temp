package billiongoods.server.warehouse;

import java.io.InputStream;

/**
 * {@code ArticleImagesManager} allows get and set image for players.
 *
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public interface ArticleImagesManager {
	void addImage(Article article, ImageType type, InputStream in);

	void removeImage(Article article, ImageType type);


	String getImage(Article article, ImageType type);

	String[] getImages(Article article, ImageType type);
}