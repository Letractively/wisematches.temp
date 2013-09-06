package billiongoods.server.services.arivals.impl;

import billiongoods.server.services.price.ExchangeManager;
import billiongoods.server.services.price.MarkupCalculator;
import billiongoods.server.warehouse.*;
import org.junit.Test;

import java.io.IOException;

import static org.easymock.EasyMock.*;
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
	public void test() throws IOException {
		final ExchangeManager exchangeManager = createMock(ExchangeManager.class);
		expect(exchangeManager.getMarkupCalculator()).andReturn(new MarkupCalculator(0.3d, 0.2d));
		replay(exchangeManager);

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
		importer.setExchangeManager(exchangeManager);
		importer.setProductManager(productManager);

//		importer.importProducts(category, getClass().getResourceAsStream("/banggood_packer.csv"));

		verify(productManager);
	}
}
