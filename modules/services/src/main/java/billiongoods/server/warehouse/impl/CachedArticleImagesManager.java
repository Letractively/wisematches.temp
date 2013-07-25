package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Article;
import billiongoods.server.warehouse.ArticleImagesManager;
import billiongoods.server.warehouse.ImageType;

import java.io.InputStream;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class CachedArticleImagesManager implements ArticleImagesManager {
	private ArticleImagesManager imagesManager;

	@Override
	public void addImage(Article article, ImageType type, InputStream in) {
	}

	public CachedArticleImagesManager() {
	}

	@Override
	public void removeImage(Article article, ImageType type) {
	}

	@Override
	public String getImage(Article article, ImageType type) {
		return null;
	}

	@Override
	public String[] getImages(Article article, ImageType type) {
		return null;
	}

	public void setImagesManager(ArticleImagesManager imagesManager) {
		this.imagesManager = imagesManager;
	}
}
