package billiongoods.server.services.image;

import billiongoods.server.warehouse.ProductDescription;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * {@code ImageManager} allows get and set image for players.
 *
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public interface ImageManager {
	void addImage(ProductDescription product, String code, InputStream in) throws IOException;

	void removeImage(ProductDescription product, String code) throws IOException;


	Collection<String> getImageCodes(ProductDescription product) throws IOException;
}