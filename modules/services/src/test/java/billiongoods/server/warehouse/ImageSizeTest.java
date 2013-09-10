package billiongoods.server.warehouse;

import billiongoods.server.services.image.ImageSize;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ImageSizeTest {
	public ImageSizeTest() {
	}

	@Test
	public void testJPG() throws IOException {
		List<Path> files = new ArrayList<>();

		for (ImageSize imageSize : ImageSize.values()) {
			final Path tempFile = Files.createTempFile("bg_resize_test_" + imageSize.getCode(), ".jpg");
			files.add(tempFile);

			try (OutputStream out = Files.newOutputStream(tempFile);
				 InputStream resourceAsStream = getClass().getResourceAsStream("/resizeTest.jpg")) {
				imageSize.scaleImage(resourceAsStream, out);
			}
		}

		for (Path file : files) {
			Files.deleteIfExists(file);
		}
	}

	@Test
	public void testPNG() throws IOException {
		List<Path> files = new ArrayList<>();

		for (ImageSize imageSize : ImageSize.values()) {
			final Path tempFile = Files.createTempFile("bg_resize_test_" + imageSize.getCode(), ".jpg");
			files.add(tempFile);

			try (OutputStream out = Files.newOutputStream(tempFile);
				 InputStream resourceAsStream = getClass().getResourceAsStream("/resizeTest.png")) {
				imageSize.scaleImage(resourceAsStream, out);
			}
		}

		for (Path file : files) {
			Files.deleteIfExists(file);
		}
	}
}
