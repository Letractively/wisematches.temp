package billiongoods.server.warehouse.impl;

import billiongoods.server.services.image.impl.FileArticleImagesManager;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class FileArticleImagesManagerTest {
	public FileArticleImagesManagerTest() {
	}

	@Test
	public void test() throws IOException {
		FileArticleImagesManager manager = new FileArticleImagesManager();
		manager.setImagesFolder(new ClassPathResource(""));
	}
}
