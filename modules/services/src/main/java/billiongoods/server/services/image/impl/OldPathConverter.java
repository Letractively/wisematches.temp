package billiongoods.server.services.image.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class OldPathConverter {
    public OldPathConverter() {
    }

    public static void main(String... args) throws Exception {
        final Path oldFolder = Paths.get(args[0]);
        final Path newFolder = Paths.get(args[1]);

        for (Path categories : Files.newDirectoryStream(oldFolder)) {
            if (Files.isDirectory(categories)) {
                for (Path product : Files.newDirectoryStream(categories)) {
                    final Integer pid = Integer.parseInt(product.getFileName().toString());

                    final Path path = resolvePath(newFolder, pid);
                    Files.createDirectories(path);

                    Files.walkFileTree(product, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            String fileName = file.getFileName().toString();
                            fileName = fileName.substring(fileName.indexOf('_') + 1);
                            final Path target = path.resolve(fileName);
                            if (!Files.exists(target)) {
                                Files.copy(file, target);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }
            }
        }
    }

    private static Path resolvePath(Path newFolder, Integer pid) {
        return newFolder.resolve(getProductImagesPath(pid));
    }

    protected static String getProductImagesPath(int id) {
        return (id / 10000) + File.separator + ((id % 10000) / 100) + File.separator + (id % 100);
    }
}