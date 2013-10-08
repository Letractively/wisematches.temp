package billiongoods.server.services.supplier.impl.banggod;

import billiongoods.server.services.supplier.Availability;
import billiongoods.server.services.supplier.SupplierDescription;
import billiongoods.server.warehouse.Supplier;
import billiongoods.server.warehouse.impl.HibernateSupplierInfo;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class BanggoodDataLoaderTest {
	public BanggoodDataLoaderTest() {
	}

	@Test
	public void test() throws Exception {
		BanggoodDataLoader dataLoader = new BanggoodDataLoader();
		dataLoader.afterPropertiesSet();

		final String response = "<html><head><meta http-equiv=\"Cache-Control\" content=\"no-cache, no-store, must-revalidate, max-age=0\"><meta http-equiv=\"Expires\" content=\"Thu, 01 Jan 1970 00:00:00 GMT\"></head><body><script language=\"JavaScript\">var strbuf = new Array();strbuf[15]='wZW5zaW';strbuf[14]='iD4uEh6';strbuf[13]='9uLVN1cH';strbuf[12]='0xMS1Gcm';strbuf[11]='LTc1Njky';strbuf[10]='TDk1OS1w';strbuf[9]='Lmh0bWw=';strbuf[8]='/j?LuyFd';strbuf[7]='dL/5ulR';strbuf[6]='bHRveXMt';strbuf[5]='9udC1TdXN';strbuf[4]='VtYmVyL';strbuf[3]='UZvci1X';strbuf[2]='POi9XbHRve';strbuf[1]='XMtTDk1OS';strbuf[0]='BvcnQtTW';var arr=[8,14,7,2,1,12,5,15,13,0,4,3,6,10,11,9];var b='';for (q = 0;q<16;q++){b+=strbuf[arr[q]];}window.location.href=b;</script></body></html>";
		final String s = dataLoader.parseJavaScriptRedirect(response);
		assertEquals("/j?LuyFdiD4uEh6dL/5ulRPOi9XbHRveXMtTDk1OS0xMS1Gcm9udC1TdXNwZW5zaW9uLVN1cHBvcnQtTWVtYmVyLUZvci1XbHRveXMtTDk1OS1wLTc1NjkyLmh0bWw=", s);
	}

	@Test
	public void testSupplierDescription() throws Exception {
		BanggoodDataLoader dataLoader = new BanggoodDataLoader();
		dataLoader.afterPropertiesSet();

//		final String referenceUri = "/Wholesale-Replacement-WLtoys-V911-2_4GHz-4CH-RC-Helicopter-BNF-New-Plug-Version-p-39473.html";
//		final String referenceUri = "New-PCB-For-WLtoys-V912-RC-Helicopter-p-83053.html";
		final String referenceUri = "/3D-Skull-Shape-Soft-Silicone-Case-Skin-Cover-For-IPhone-5-5G-p-85268.html";
		final HibernateSupplierInfo supplierInfo = new HibernateSupplierInfo(referenceUri, "SKU044199 ", Supplier.BANGGOOD, null);

		final SupplierDescription desc = dataLoader.loadDescription(supplierInfo);
		assertNotNull(desc);
		assertNotNull(desc.getPrice());
		System.out.println(desc);
	}

	@Test
	public void testAvailability() throws Exception {
		BanggoodDataLoader dataLoader = new BanggoodDataLoader();
		dataLoader.initialize();

		final String referenceUri = "/Wltoys-L202-2_4G-1-12-Brushless-Remote-Comtrol-RC-Racing-Car-p-71969.html";
		final HibernateSupplierInfo supplierInfo = new HibernateSupplierInfo(referenceUri, "SKU063099", Supplier.BANGGOOD, null);

		final Availability availability = dataLoader.loadAvailability(supplierInfo);
		assertNotNull(availability);
		System.out.println(availability);
	}

	@Test
	public void testAvailability2() throws Exception {
		BanggoodDataLoader dataLoader = new BanggoodDataLoader();
		dataLoader.initialize();

		final String referenceUri = "/Hubsan-X4-H107C-2_4G-4CH-RC-Quadcopter-With-Camera-Mode-2-RTF-p-75824.html";
		final HibernateSupplierInfo supplierInfo = new HibernateSupplierInfo(referenceUri, "SKU078173", Supplier.BANGGOOD, null);

		final Availability availability = dataLoader.loadAvailability(supplierInfo);
		assertNotNull(availability);
		System.out.println(availability);
	}

	@Test
	public void testAvailability3() throws Exception {
		BanggoodDataLoader dataLoader = new BanggoodDataLoader();
		dataLoader.initialize();

		final String referenceUri = "/Wholesale-2012-Hot-Sale-New-Kids-Toys-2010E-Q2-Mini-RC-Stunt-Car-Toys-For-Children-p-50064.html";
		final HibernateSupplierInfo supplierInfo = new HibernateSupplierInfo(referenceUri, "SKU041071", Supplier.BANGGOOD, null);

		final Availability availability = dataLoader.loadAvailability(supplierInfo);
		assertNotNull(availability);
		System.out.println(availability);
	}
}
