package billiongoods.server.warehouse.impl;

import billiongoods.server.services.image.ImageSize;
import billiongoods.server.services.image.impl.FileImageManager;
import billiongoods.server.services.image.impl.FileImageResolver;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.ProductPreview;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class FileImageManagerTest {
	private Path workingFolder;
	private FileImageManager manager;
	private FileImageResolver imageResolver;

	private ProductPreview description;

	public FileImageManagerTest() {
	}

	@Before
	public void setUp() throws IOException {
		final Category category = createMock(Category.class);
		expect(category.getId()).andReturn(13).anyTimes();
		replay(category);

		description = createMock(ProductPreview.class);
		expect(description.getId()).andReturn(123).anyTimes();
		expect(description.getCategoryId()).andReturn(13).anyTimes();
		replay(description);

		workingFolder = Files.createTempDirectory("billiongoods");

		imageResolver = new FileImageResolver();
		imageResolver.setImagesFolder(new FileSystemResource(workingFolder.toFile()));

		manager = new FileImageManager();
		manager.setImageResolver(imageResolver);
	}

	@Test
	public void test() throws IOException {
		assertEquals(0, manager.getImageCodes(description).size());

		manager.addImage(description, "1", FileImageManagerTest.class.getResourceAsStream("/test.jpg"));
		assertEquals(1, manager.getImageCodes(description).size());

		for (ImageSize size : ImageSize.values()) {
			assertTrue(Files.exists(imageResolver.resolveFile(description, "1", size)));
		}

		manager.addImage(description, "2", FileImageManagerTest.class.getResourceAsStream("/test.jpg"));
		assertEquals(2, manager.getImageCodes(description).size());

		for (ImageSize size : ImageSize.values()) {
			assertTrue(Files.exists(imageResolver.resolveFile(description, "1", size)));
			assertTrue(Files.exists(imageResolver.resolveFile(description, "2", size)));
		}

		final Collection<String> images = manager.getImageCodes(description);
		assertTrue(images.contains("1"));
		assertTrue(images.contains("2"));

		manager.removeImage(description, "1");
		assertEquals(1, manager.getImageCodes(description).size());

		for (ImageSize size : ImageSize.values()) {
			assertFalse(Files.exists(imageResolver.resolveFile(description, "1", size)));
			assertTrue(Files.exists(imageResolver.resolveFile(description, "2", size)));
		}

		manager.removeImage(description, "2");
		assertEquals(0, manager.getImageCodes(description).size());

		for (ImageSize size : ImageSize.values()) {
			assertFalse(Files.exists(imageResolver.resolveFile(description, "1", size)));
			assertFalse(Files.exists(imageResolver.resolveFile(description, "2", size)));
		}
	}

	@After
	public void tearDown() throws IOException {
		Files.walkFileTree(workingFolder, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				if (exc == null) {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				} else {
					throw exc;
				}
			}
		});
	}
}
