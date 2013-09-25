package billiongoods.server.services.image.impl;

import billiongoods.server.services.image.ImageResolver;
import billiongoods.server.services.image.ImageSize;
import billiongoods.server.warehouse.ProductImager;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class FileImageResolver implements ImageResolver {
	private Path imagesFolder;

	public FileImageResolver() {
	}

	@Override
	public String resolveURI(ProductImager imager, String code, ImageSize size) {
		return imager.getCategoryId() + File.separator + imager.getId() + File.separator + imager.getId() + "_" + code + (size != null ? "_" + size.getCode() : "") + ".jpg";
	}

	@Override
	public Path resolvePath(ProductImager imager) {
		return imagesFolder.resolve(imager.getCategoryId() + File.separator + imager.getId());
	}

	@Override
	public Path resolveFile(ProductImager imager, String code, ImageSize size) {
		return imagesFolder.resolve(resolveURI(imager, code, size));
	}

	public void setImagesFolder(Resource imagesFolder) throws IOException {
		this.imagesFolder = imagesFolder.getFile().toPath();

		Files.createDirectories(this.imagesFolder);
	}
}
