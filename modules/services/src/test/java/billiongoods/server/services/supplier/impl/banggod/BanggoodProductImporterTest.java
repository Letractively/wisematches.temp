package billiongoods.server.services.supplier.impl.banggod;

import billiongoods.server.services.price.PriceConverter;
import billiongoods.server.services.price.impl.HibernatePriceConverter;
import billiongoods.server.warehouse.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Ignore
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
	public void test() throws IOException {
		final PriceConverter priceConverter = new HibernatePriceConverter(34.2d);

		final Category category = createMock(Category.class);
		replay(category);

		final Product product = createMock(Product.class);
		expect(product.getId()).andReturn(13);
		replay(product);

		final ProductEditor editor = new ProductEditor();
		editor.setName("WLtoys V911-1 RC Helicopter Spare Parts Green Main Blade V911-1-2");
		editor.setDescription("<br /><span style=\"font-size:12px;\"><span style=\"font-family: arial-helvetica-sans-serif;\"><strong>WLtoys V911-1 RC Helicopter Spare Parts Green Main Blade V911-1-2</strong><br /><br /><strong>Description:</strong><br /><br />Brand: WLtoys<br />Item Name: Main Blade<br />NO.: V911-1-2<br />Usage:For WLtoys V911-1 RC Helicopter<br /><br /><strong>Package Included:</strong><br />1 x Main Blade</span></span>");
		editor.setCategoryId(13);
		editor.setPrice(new Price(1.84d, null));
		editor.setWeight(0.05d);
		editor.setReferenceUri("82283");
		editor.setReferenceCode("SKU088161");
		editor.setWholesaler(Supplier.BANGGOOD);
		editor.setSupplierPrice(new Price(1.28d, null));

		final ProductManager productManager = createMock(ProductManager.class);
		expect(productManager.createProduct(editor)).andReturn(product);
		replay(productManager);

		final BanggoodProductImporter importer = new BanggoodProductImporter();
		importer.setProductManager(productManager);
		importer.setPriceConverter(priceConverter);

//		importer.importProducts(category, getClass().getResourceAsStream("/banggood_packer.csv"));

		verify(productManager);
	}

	@Test
	@Ignore
	public void importImages() throws IOException {
		final BanggoodProductImporter importer = new BanggoodProductImporter();

		final InputStream in = new FileInputStream("C:\\Users\\klimese\\Downloads\\120854_product_image.csv");

		final Path out = Paths.get("C:\\Temp\\qwe");

		final Map<String, Set<String>> stringSetMap = importer.parseImages(in);
		for (Map.Entry<String, Set<String>> entry : stringSetMap.entrySet()) {
			final String sku = entry.getKey();
			final Set<String> urls = entry.getValue();

			final Path directory = Files.createDirectories(out.resolve(sku));

			int i = 0;
			for (String url : urls) {
				final URL u = new URL(url);
				final InputStream in1 = u.openStream();

				final String ext = url.substring(url.lastIndexOf("."));

				final Path file = Files.createFile(directory.resolve(String.valueOf(i++) + ext));
				Files.copy(in1, file, StandardCopyOption.REPLACE_EXISTING);
				in1.close();
			}
		}

		in.close();
	}
}
