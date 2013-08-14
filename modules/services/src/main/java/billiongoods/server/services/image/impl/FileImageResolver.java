package billiongoods.server.services.image.impl;

import billiongoods.server.services.image.ImageResolver;
import billiongoods.server.services.image.ImageSize;
import billiongoods.server.warehouse.ArticleDescription;
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
	public String resolveURI(ArticleDescription article, String code, ImageSize size) {
		return article.getCategory().getId() + File.separator + article.getId() + File.separator + article.getId() + "_" + code + (size != null ? "_" + size.getCode() : "") + ".jpg";
	}

	@Override
	public Path resolvePath(ArticleDescription article) {
		return imagesFolder.resolve(article.getCategory().getId() + File.separator + article.getId());
	}

	@Override
	public Path resolveFile(ArticleDescription article, String code, ImageSize size) {
		return imagesFolder.resolve(resolveURI(article, code, size));
	}

	public void setImagesFolder(Resource imagesFolder) throws IOException {
		this.imagesFolder = imagesFolder.getFile().toPath();

		Files.createDirectories(this.imagesFolder);
	}
}
