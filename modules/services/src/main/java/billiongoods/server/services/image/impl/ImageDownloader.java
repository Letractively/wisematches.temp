package billiongoods.server.services.image.impl;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ImageDownloader {
	public ImageDownloader() {
	}

	public static void main(String[] args) throws IOException {
		final List<String> strings = FileUtils.readLines(new File(args[0]));

		int number = 0;
		int startPoint = 0;

		Path outPath = null;

		final Map<String, AtomicInteger> skus = new HashMap<>();


		final Path outFolder = new File(args[1]).toPath();
		for (String line : strings) {
			if (number < startPoint) {
				number++;
				continue;
			}

			try {
				final String[] split = line.split(",");

				final String sku = split[0].trim();
				final String url = split[1].trim();

				AtomicInteger atomicInteger = skus.get(sku);
				if (atomicInteger == null) {
					outPath = outFolder.resolve(sku);
					Files.createDirectories(outPath);

					atomicInteger = new AtomicInteger();
					skus.put(sku, atomicInteger);
				}

				final Path resolve = outPath.resolve(String.valueOf(atomicInteger.incrementAndGet()) + ".jpg");

				try (InputStream inputStream = new URL(url).openStream()) {
					System.out.println("[" + number + " of " + strings.size() + " ] Copy image from " + url + " to " + resolve);
					Files.copy(inputStream, resolve, StandardCopyOption.REPLACE_EXISTING);
				}
			} catch (Exception ex) {
				System.out.println("ERROR: Data can't be loaded from: " + line + ". " + ex.getMessage());
			} finally {
				number++;
			}
		}
	}
}
