package billiongoods.server.services.image;

import billiongoods.server.warehouse.ProductPreview;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * {@code ImageManager} allows get and set image for players.
 *
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public interface ImageManager {
	void addImage(ProductPreview product, String code, InputStream in) throws IOException;

	void removeImage(ProductPreview product, String code) throws IOException;


	Collection<String> getImageCodes(ProductPreview product) throws IOException;
}