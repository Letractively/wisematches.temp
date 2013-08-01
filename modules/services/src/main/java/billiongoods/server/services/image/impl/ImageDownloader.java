package billiongoods.server.services.image.impl;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class ImageDownloader {
    public ImageDownloader() {
    }

    public static void main(String[] args) throws IOException {
        final Path outFolder = new File(args[1]).toPath();
        final BufferedReader reader = new BufferedReader(new FileReader(args[0]));

        int index = 0;
        Path outPath = null;
        String currentSku = null;

        String line = reader.readLine();
        while (line != null) {
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
                System.out.println("Copy image from " + url + " to " + resolve);
                Files.copy(inputStream, resolve, StandardCopyOption.REPLACE_EXISTING);
            }

            line = reader.readLine();
        }
    }
}
