package billiongoods.server.services.image.impl;

import billiongoods.server.services.image.ImageResolver;
import billiongoods.server.services.image.ImageSize;
import billiongoods.server.warehouse.ProductDescription;
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
	public String resolveURI(ProductDescription product, String code, ImageSize size) {
		return product.getCategoryId() + File.separator + product.getId() + File.separator + product.getId() + "_" + code + (size != null ? "_" + size.getCode() : "") + ".jpg";
	}

	@Override
	public Path resolvePath(ProductDescription product) {
		return imagesFolder.resolve(product.getCategoryId() + File.separator + product.getId());
	}

	@Override
	public Path resolveFile(ProductDescription product, String code, ImageSize size) {
		return imagesFolder.resolve(resolveURI(product, code, size));
	}

	public void setImagesFolder(Resource imagesFolder) throws IOException {
		this.imagesFolder = imagesFolder.getFile().toPath();

		Files.createDirectories(this.imagesFolder);
	}
}
