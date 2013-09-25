package billiongoods.server.services.image;

import billiongoods.server.warehouse.ProductImager;

import java.nio.file.Path;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface ImageResolver {
	Path resolvePath(ProductImager imager);

	Path resolveFile(ProductImager imager, String code, ImageSize size);

	String resolveURI(ProductImager imager, String code, ImageSize size);
}
