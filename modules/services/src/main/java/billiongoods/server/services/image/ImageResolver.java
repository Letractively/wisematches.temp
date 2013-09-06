package billiongoods.server.services.image;

import billiongoods.server.warehouse.ProductDescription;

import java.nio.file.Path;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ImageResolver {
	Path resolvePath(ProductDescription product);

	Path resolveFile(ProductDescription product, String code, ImageSize size);

	String resolveURI(ProductDescription product, String code, ImageSize size);
}
