package billiongoods.server.services.image.impl;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ImageDownloader {
    public ImageDownloader() {
    }

    public static void main(String[] args) throws IOException {
        final List<String> strings = FileUtils.readLines(new File(args[0]));

        int index = 0;
        int number = 0;
        Path outPath = null;
        String currentSku = null;

        final Path outFolder = new File(args[1]).toPath();
        for (String line : strings) {
            final String[] split = line.split(",");

            final String sku = split[0];
            final String url = split[1];

            if (!sku.equalsIgnoreCase(currentSku)) { // new sku
                index = 0;
                outPath = outFolder.resolve(sku);
                Files.createDirectories(outPath);
                currentSku = sku;
            }

            final Path resolve = outPath.resolve(String.valueOf(index++) + ".jpg");

            try (InputStream inputStream = new URL(url).openStream()) {
                System.out.println("[" + (number++) + " of " + strings.size() + " ] Copy image from " + url + " to " + resolve);
                Files.copy(inputStream, resolve, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}
