package billiongoods.server.services.supplier.impl.banggod;

import billiongoods.server.services.supplier.SupplierDescription;
import billiongoods.server.warehouse.Supplier;
import billiongoods.server.warehouse.impl.HibernateSupplierInfo;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Ignore
public class BanggoodDataLoaderTest {
	public BanggoodDataLoaderTest() {
	}

	@Test
	public void test_parseJavaScriptRedirect() throws Exception {
		BanggoodDataLoader dataLoader = new BanggoodDataLoader();
		dataLoader.afterPropertiesSet();

		final String response = "<html><head><meta http-equiv=\"Cache-Control\" content=\"no-cache, no-store, must-revalidate, max-age=0\"><meta http-equiv=\"Expires\" content=\"Thu, 01 Jan 1970 00:00:00 GMT\"></head><body><script language=\"JavaScript\">var strbuf = new Array();strbuf[15]='wZW5zaW';strbuf[14]='iD4uEh6';strbuf[13]='9uLVN1cH';strbuf[12]='0xMS1Gcm';strbuf[11]='LTc1Njky';strbuf[10]='TDk1OS1w';strbuf[9]='Lmh0bWw=';strbuf[8]='/j?LuyFd';strbuf[7]='dL/5ulR';strbuf[6]='bHRveXMt';strbuf[5]='9udC1TdXN';strbuf[4]='VtYmVyL';strbuf[3]='UZvci1X';strbuf[2]='POi9XbHRve';strbuf[1]='XMtTDk1OS';strbuf[0]='BvcnQtTW';var arr=[8,14,7,2,1,12,5,15,13,0,4,3,6,10,11,9];var b='';for (q = 0;q<16;q++){b+=strbuf[arr[q]];}window.location.href=b;</script></body></html>";
		final String s = dataLoader.parseJavaScriptRedirect(response);
		assertEquals("/j?LuyFdiD4uEh6dL/5ulRPOi9XbHRveXMtTDk1OS0xMS1Gcm9udC1TdXNwZW5zaW9uLVN1cHBvcnQtTWVtYmVyLUZvci1XbHRveXMtTDk1OS1wLTc1NjkyLmh0bWw=", s);
	}

	@Test
	public void test_parseStockInfo() throws Exception {
		BanggoodDataLoader dataLoader = new BanggoodDataLoader();

		// expect restock
		assertEquals(1356897600000L, dataLoader.parseStockInfo("expect restock on 31st december 2012").getRestockDate().getTime());

		// expected restock
		assertEquals(1356897600000L, dataLoader.parseStockInfo("expected restock on 31st december 2012").getRestockDate().getTime());

		// expected restock
		assertEquals(1392840000000L, dataLoader.parseStockInfo("Out of stock , expect restock on 20th February").getRestockDate().getTime());
	}

	@Test
	public void testSupplierDescription() throws Exception {
		BanggoodDataLoader dataLoader = new BanggoodDataLoader();
		dataLoader.afterPropertiesSet();

		dataLoader.initialize();

		final HibernateSupplierInfo info1 = new HibernateSupplierInfo("/3D-Skull-Shape-Soft-Silicone-Case-Skin-Cover-For-IPhone-5-5G-p-85268.html", "SKU044199", Supplier.BANGGOOD, null);
		final SupplierDescription desc1 = dataLoader.loadDescription(info1);
		assertNotNull(desc1);
		assertNotNull(desc1.getPrice());
		System.out.println(desc1);

		final HibernateSupplierInfo info2 = new HibernateSupplierInfo("/Wholesale-Replacement-WLtoys-V911-2_4GHz-4CH-RC-Helicopter-BNF-New-Plug-Version-p-39473.html", "SKU043151", Supplier.BANGGOOD, null);
		final SupplierDescription desc2 = dataLoader.loadDescription(info2);
		assertNotNull(desc2);
		assertNotNull(desc2.getPrice());
		System.out.println(desc2);
	}
}
