package billiongoods.server.services.image.impl;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;

/**
 * Enumeration that contains availeable sizes of player images.
 */
@Deprecated
public enum ImageSize {
	REAL(-1, -1),
	GRID(150, 150),
	THUMB(50, 50),
	PREVIEW(280, 280),
	VIEW(420, 420);

	private final int width;
	private final int height;

	ImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Scales specified image from input stream to output stream according to this type.
	 * <p/>
	 * If type is {@code REAL} when image is just copied from input stream to output.
	 * <p/>
	 * <p/>
	 * Image is scaled using {@code BICUBIC} interpolation.
	 * <p/>
	 * This method does not closes input and output stream.
	 *
	 * @param iis the input image stream
	 * @param ios the output image stream
	 * @throws NullPointerException if input or output stream is {@code null}
	 * @throws IOException          if image can't be readed from input stream or can't be writed into output stream.
	 * @see RenderingHints#VALUE_INTERPOLATION_BICUBIC
	 */
	public void scaleImage(ImageInputStream iis, ImageOutputStream ios) throws IOException {
		final Iterator<ImageReader> iterator = ImageIO.getImageReaders(iis);
		if (!iterator.hasNext()) {
			throw new IOException("Unknown type of input image");
		}

		final ImageReader reader = iterator.next();
		final ImageWriter writer = ImageIO.getImageWriter(reader);
		if (writer == null) {
			throw new IOException("Where is not writer for specified input image");
		}

		try {
			reader.setInput(iis, true, true);
			writer.setOutput(ios);

			final BufferedImage image = reader.read(0, reader.getDefaultReadParam());
			final BufferedImage scaled = scaleImage(image);
			writer.write(scaled);
		} finally {
			reader.dispose();
			writer.dispose();
		}
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
		if (this == REAL) {
			return image;
		}

		final float scale = getScaleFactor(image);
		final int w = (int) (scale * image.getWidth());
		final int h = (int) (scale * image.getHeight());

		final BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
