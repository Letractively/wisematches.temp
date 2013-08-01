package billiongoods.server.services.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Enumeration that contains availeable sizes of player images.
 */
public enum ImageSize {
	TINY(50, 50),
	SMALL(150, 150),
	MEDIUM(350, 350),
	LARGE(600, 600);

	private final int width;
	private final int height;
	private final String code;

	ImageSize(int width, int height) {
		this.width = width;
		this.height = height;
		this.code = name().substring(0, 1);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getCode() {
		return code;
	}

	public void scaleImage(InputStream in, OutputStream out) throws IOException {
		final BufferedImage read = ImageIO.read(in);
		final BufferedImage bufferedImage = scaleImage(read);
		ImageIO.write(bufferedImage, "JPEG", out);
	}

	private float getScaleFactor(BufferedImage image) {
		final float w = image.getWidth();
		final float h = image.getHeight();
		if (w > h) {
			return width / w;
		} else {
			return height / h;
		}
	}

	private BufferedImage scaleImage(BufferedImage image) {
		if (image == null) {
			throw new NullPointerException("Image can't be null");
		}

		final float scale = getScaleFactor(image);
		final int w = (int) (scale * image.getWidth());
		final int h = (int) (scale * image.getHeight());

		final BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		final Graphics2D bg = scaled.createGraphics();
		bg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		// scale image
		final AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		at.translate((width - w) / 2, (height - h) / 2);
		bg.drawRenderedImage(image, at);
		bg.dispose();
		return scaled;
	}
}
