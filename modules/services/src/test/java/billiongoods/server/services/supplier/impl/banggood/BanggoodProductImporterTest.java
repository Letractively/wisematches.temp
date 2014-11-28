package billiongoods.server.services.supplier.impl.banggood;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodProductImporterTest {
	public BanggoodProductImporterTest() {
	}

	@Test
	public void testSpanCleaner() {
		final BanggoodProductImporter importer = new BanggoodProductImporter();
		String s = importer.cleanSpan("<br /><span style=\"font-size:12px;\"><span style=\"font-family: arial-helvetica-sans-serif;\"><strong>WLtoys V911-1 RC Helicopter Spare Parts Green Main Blade V911-1-2</strong><br /><br /><strong>Description:</strong><br /><br />Brand: WLtoys<br />Item Name: Main Blade<br />NO.: V911-1-2<br />Usage:For WLtoys V911-1 RC Helicopter<br /><br /><strong>Package Included:</strong><br />1 x Main Blade</span></span>");
		assertEquals("<br /><strong>WLtoys V911-1 RC Helicopter Spare Parts Green Main Blade V911-1-2</strong><br /><br /><strong>Description:</strong><br /><br />Brand: WLtoys<br />Item Name: Main Blade<br />NO.: V911-1-2<br />Usage:For WLtoys V911-1 RC Helicopter<br /><br /><strong>Package Included:</strong><br />1 x Main Blade", s);
	}

	@Test
	public void importImages() throws IOException {
		final BanggoodProductImporter importer = new BanggoodProductImporter();
		try (InputStream in = getClass().getResourceAsStream("/product_image.csv")) {
			final Map<String, Set<String>> stringSetMap = importer.parseImages(in);
			assertEquals(1659, stringSetMap.size());
		}
	}

	@Test
	public void importProducts() throws IOException {
		final BanggoodProductImporter importer = new BanggoodProductImporter();
		try (InputStream in = getClass().getResourceAsStream("/product_info.csv")) {
			final List<BanggoodProductImporter.SuppliedProduct> suppliedProducts = importer.parseProducts(in);
			assertEquals(2617, suppliedProducts.size());
		}
	}
}
