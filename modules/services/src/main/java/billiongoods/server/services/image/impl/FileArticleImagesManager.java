package billiongoods.server.services.image.impl;

import billiongoods.server.services.image.ArticleImagesManager;
import billiongoods.server.services.image.ImageResolver;
import billiongoods.server.services.image.ImageType;
import billiongoods.server.warehouse.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Realization of {@code ArticleImagesManager} that saves images into files.
 * <p/>
 * A name of the file is composed by following rule: {@code <PlayerID>_<ImageSize>.image} where:
 * {@code <PlayerID>} is player id, {@code <ImageSize>} the image type (see {@code ImageSize}).
 *
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public class FileArticleImagesManager implements ArticleImagesManager {
	private Resource imagesFolder;
	private ImageResolver imageResolver;

	private static final FilenameFilter JPEG = new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(".jpg");
		}
	};
	private static final String[] NO_IMAGES_RES = new String[0];

	private static final Logger log = LoggerFactory.getLogger("wisematches.player.ImageManager");

	public FileArticleImagesManager() {
	}

	@Override
	public void addImage(Article article, ImageType type, InputStream in) {
	}

	@Override
	public void removeImage(Article article, ImageType type) {
	}

	@Override
	public String getImage(Article article, ImageType type) {
		final String relativePath = imageResolver.resolveImagePath(article, "", type);
		return relativePath + "/" + article.getId() + ".jpg";
	}

	@Override
	public String[] getImages(Article article, ImageType type) {
		final String relativePath = imageResolver.resolveImagePath(article, "", type);
		try {
			final File file = new File(imagesFolder.getFile(), relativePath);
			final String[] list = file.list(JPEG);

			final String[] res = new String[list.length];
			for (int i = 0; i < list.length; i++) {
				res[i] = relativePath + "/" + list[i];
			}
			return res;
		} catch (IOException ex) {
			return NO_IMAGES_RES;
		}
	}
/*


	public InputStream getPlayerImage(long playerId, ImageType type) {
		if (type == null) {
			throw new NullPointerException("Type can't be null");
		}

		try {
			final File file = new File(imagesFolder.getFile(), getImageFilename(playerId, type));
			if (!file.exists()) {
				return null;
			}

			return new FileInputStream(file);
		} catch (IOException e) {
			log.warn("We checked that file exist but 'FileNotFoundException' exception received", e);
			return null;
		}
	}

	*/
/**
 * {@inheritDoc}
 *//*

	public void removePlayerImage(long playerId, ImageType type) {
		if (type == null) {
			throw new NullPointerException("Type can't be null");
		}

		try {
			final File f = new File(imagesFolder.getFile(), getImageFilename(playerId, type));
			if (f.exists()) {
				if (f.delete()) {
				} else {
					log.warn("Player image can't be deleted by system error");
				}
			}
		} catch (IOException ex) {
			log.error("Image file can't be deleted", ex);
		}
	}

	*/

	/**
	 * {@inheritDoc}
	 *//*

	public void setPlayerImage(long playerId, InputStream stream, ImageType type)
			throws IOException {
		if (stream == null) {
			throw new NullPointerException("Stream can't be null");
		}
		if (type == null) {
			throw new NullPointerException("Type can't be null");
		}

		final File f = new File(imagesFolder.getFile(), getImageFilename(playerId, type));
		log.debug("Update player image file: {} ({})", f, f.getAbsolutePath());

		boolean added = false;
		if (!f.exists()) {
			if (!f.createNewFile()) {
				throw new IOException("Image file " + f + " can't be created");
			}
			added = true;
		}

		log.debug("Update image for player {} of type {}", playerId, type);

		try (FileOutputStream out = new FileOutputStream(f)) {
			FileCopyUtils.copy(stream, out);
		}

*/
/*
		final ImageOutputStream ios = ImageIO.createImageOutputStream(f);
		final ImageInputStream iis = ImageIO.createImageInputStream(stream);
		try {
			type.scaleImage(iis, ios);

			if (added) {
				for (PlayerImagesListener listener : listeners) {
					listener.playerImageAdded(playerId, type);
				}
			} else {
				for (PlayerImagesListener listener : listeners) {
					listener.playerImageUpdated(playerId, type);
				}
			}
		} finally {
			ios.close();
			iis.close();
		}
*//*

	}
*/
	public void setImagesFolder(Resource imagesFolder) throws IOException {
		this.imagesFolder = imagesFolder;

		log.debug("Change images folder to {}. Absolute path: {}", imagesFolder, imagesFolder.getFile().getAbsolutePath());
		if (!imagesFolder.exists()) {
			if (imagesFolder.getFile().mkdirs()) {
				log.debug("images folder created");
			} else {
				log.warn("Images folder can't be created by system error");
			}
		}
	}

	public void setImageResolver(ImageResolver imageResolver) {
		this.imageResolver = imageResolver;
	}
}
