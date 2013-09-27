package billiongoods.server.services.supplier.impl.banggod;

import billiongoods.server.services.supplier.DataLoadingException;
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
		BanggoodDataLoader priceValidator = new BanggoodDataLoader();

		final String response = "<html><head><meta http-equiv=\"Cache-Control\" content=\"no-cache, no-store, must-revalidate, max-age=0\"><meta http-equiv=\"Expires\" content=\"Thu, 01 Jan 1970 00:00:00 GMT\"></head><body><script language=\"JavaScript\">var strbuf = new Array();strbuf[15]='wZW5zaW';strbuf[14]='iD4uEh6';strbuf[13]='9uLVN1cH';strbuf[12]='0xMS1Gcm';strbuf[11]='LTc1Njky';strbuf[10]='TDk1OS1w';strbuf[9]='Lmh0bWw=';strbuf[8]='/j?LuyFd';strbuf[7]='dL/5ulR';strbuf[6]='bHRveXMt';strbuf[5]='9udC1TdXN';strbuf[4]='VtYmVyL';strbuf[3]='UZvci1X';strbuf[2]='POi9XbHRve';strbuf[1]='XMtTDk1OS';strbuf[0]='BvcnQtTW';var arr=[8,14,7,2,1,12,5,15,13,0,4,3,6,10,11,9];var b='';for (q = 0;q<16;q++){b+=strbuf[arr[q]];}window.location.href=b;</script></body></html>";
		final String s = priceValidator.parseJavaScriptRedirect(response);
		assertEquals("/j?LuyFdiD4uEh6dL/5ulRPOi9XbHRveXMtTDk1OS0xMS1Gcm9udC1TdXNwZW5zaW9uLVN1cHBvcnQtTWVtYmVyLUZvci1XbHRveXMtTDk1OS1wLTc1NjkyLmh0bWw=", s);
	}

	@Test
	public void asd() throws DataLoadingException {
		BanggoodDataLoader priceValidator = new BanggoodDataLoader();

//		final String referenceUri = "/Wholesale-Replacement-WLtoys-V911-2_4GHz-4CH-RC-Helicopter-BNF-New-Plug-Version-p-39473.html";
		final String referenceUri = "/Waterproof-Shockproof-Dirt-Snow-Proof-Case-Cover-For-IPhone-5-p-80768.html";
		final HibernateSupplierInfo supplierInfo = new HibernateSupplierInfo(referenceUri, "SKU044199 ", Supplier.BANGGOOD, null);
		final SupplierDescription desc = priceValidator.loadDescription(supplierInfo);
		assertNotNull(desc);
		System.out.println(desc);
	}
}
