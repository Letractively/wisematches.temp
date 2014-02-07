package billiongoods.server.services.image.impl;

import billiongoods.server.warehouse.ProductImager;
import org.junit.Test;

import java.nio.file.Paths;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class FileImageResolverTest {
	public FileImageResolverTest() {
	}

	@Test
	public void test() {
		final ProductImager pi = createMock(ProductImager.class);
		expect(pi.getId()).andReturn(1).andReturn(12).andReturn(123).andReturn(1234).andReturn(12345).andReturn(123456);
		replay(pi);

		final FileImageResolver r = new FileImageResolver();

		assertEquals(Paths.get("0", "0", "1").toString(), r.getProductImagesPath(pi));
		assertEquals(Paths.get("0", "0", "12").toString(), r.getProductImagesPath(pi));
		assertEquals(Paths.get("0", "1", "23").toString(), r.getProductImagesPath(pi));
		assertEquals(Paths.get("0", "12", "34").toString(), r.getProductImagesPath(pi));
		assertEquals(Paths.get("1", "23", "45").toString(), r.getProductImagesPath(pi));
		assertEquals(Paths.get("12", "34", "56").toString(), r.getProductImagesPath(pi));
	}
}
