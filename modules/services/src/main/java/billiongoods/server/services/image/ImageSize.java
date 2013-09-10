package billiongoods.server.services.image;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Enumeration that contains availeable sizes of player images.
 */
public enum ImageSize {
	TINY(50, 50, .9f),
	SMALL(150, 150, .85f),
	MEDIUM(280, 280, .85f),
	LARGE(600, 600, .8f);

	private final int width;
	private final int height;
	private final String code;
	private final float compression;

	ImageSize(int width, int height, float compression) {
		this.width = width;
		this.height = height;
		this.compression = compression;
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
		final ImageInputStream imgIn = ImageIO.createImageInputStream(in);
		final ImageReader imgReader = ImageIO.getImageReaders(imgIn).next();

		final ImageWriter imgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
		final ImageOutputStream imgOut = ImageIO.createImageOutputStream(out);

		imgReader.setInput(imgIn);
		imgWriter.setOutput(imgOut);

		final BufferedImage read = imgReader.read(0);
		final BufferedImage bufferedImage = scaleImage(read);

		final IIOImage image = new IIOImage(bufferedImage, null, null);

		final JPEGImageWriteParam params = (JPEGImageWriteParam) imgWriter.getDefaultWriteParam();
		params.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
		params.setCompressionQuality(compression);
		params.setOptimizeHuffmanTables(true);

		imgWriter.write(null, image, params);

		imgIn.close();
		imgOut.close();

		imgWriter.dispose();
		imgReader.dispose();
	}

	private double getScaleFactor(BufferedImage image) {
		final double w = image.getWidth();
		final double h = image.getHeight();
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

		final double scale = getScaleFactor(image);
		final int w = (int) (scale * image.getWidth());
		final int h = (int) (scale * image.getHeight());

		final Image scaledInstance = image.getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING);

		final BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		final Graphics2D bg = scaled.createGraphics();
		bg.setBackground(Color.WHITE);
		bg.clearRect(0, 0, width, height);
		bg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		bg.drawImage(scaledInstance, (width - w) / 2, (height - h) / 2, w, h, null);
		bg.dispose();

		return scaled;
	}
}
