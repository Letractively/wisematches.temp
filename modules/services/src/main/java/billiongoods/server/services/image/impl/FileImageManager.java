package billiongoods.server.services.image.impl;

import billiongoods.server.services.image.ImageManager;
import billiongoods.server.services.image.ImageResolver;
import billiongoods.server.services.image.ImageSize;
import billiongoods.server.warehouse.ProductPreview;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Realization of {@code ImageManager} that saves images into files.
 * <p/>
 * A name of the file is composed by following rule: {@code <PlayerID>_<ImageSize>.image} where:
 * {@code <PlayerID>} is player id, {@code <ImageSize>} the image type (see {@code ImageSize}).
 *
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public class FileImageManager implements ImageManager {
	private ImageResolver imageResolver;

	public FileImageManager() {
	}

	@Override
	public void addImage(ProductPreview product, String code, InputStream in) throws IOException {
		Files.createDirectories(imageResolver.resolvePath(product));

		final Path originalFiles = imageResolver.resolveFile(product, code, null);
		Files.copy(in, originalFiles, StandardCopyOption.REPLACE_EXISTING);

		for (ImageSize size : ImageSize.values()) {
			final Path path = imageResolver.resolveFile(product, code, size);
			Files.deleteIfExists(path);

			try (FileInputStream iis = new FileInputStream(originalFiles.toFile());
				 FileOutputStream ios = new FileOutputStream(path.toFile())) {
				size.scaleImage(iis, ios);
			}
		}
	}

	@Override
	public void removeImage(ProductPreview product, String code) throws IOException {
		final Path originalFiles = imageResolver.resolveFile(product, code, null);
		Files.deleteIfExists(originalFiles);

		for (ImageSize size : ImageSize.values()) {
			final Path path = imageResolver.resolveFile(product, code, size);
			Files.deleteIfExists(path);
		}
	}

	@Override
	public Collection<String> getImageCodes(ProductPreview product) throws IOException {
		final Path path = imageResolver.resolvePath(product);
		if (Files.notExists(path)) {
			return Collections.emptySet();
		}

		final Pattern p = Pattern.compile("([^_]*)\\.jpg");

		final Set<String> res = new HashSet<>(path.getNameCount());
		DirectoryStream<Path> ds = Files.newDirectoryStream(path, "*.jpg");
		for (Path file : ds) {
			final String fileName = file.getFileName().toString();

			final Matcher matcher = p.matcher(fileName);
			if (matcher.matches()) {
				res.add(matcher.group(1));
			}
		}
		ds.close();
		return res;
	}

	public void setImageResolver(ImageResolver imageResolver) {
		this.imageResolver = imageResolver;
	}
}
