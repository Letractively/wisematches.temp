package billiongoods.server.services.supplier.impl.banggood;

import billiongoods.server.services.supplier.DataLoadingException;
import billiongoods.server.services.supplier.SupplierDescription;
import billiongoods.server.warehouse.Supplier;
import billiongoods.server.warehouse.impl.HibernateSupplierInfo;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Ignore
public class BanggoodMobileDataLoaderTest {
	public BanggoodMobileDataLoaderTest() {
	}

	@Test
	public void test_parseStockMsg() throws DataLoadingException {
		final BanggoodMobileDataLoader dataLoader = new BanggoodMobileDataLoader();
		final Map<String, String> stringStringMap = dataLoader.parseStockMsg("{\"sku_or_poa\":\"POA184065\",\"products_id\":\"916095\",\"quantity_status\":\"11\",\"supply_type\":\"0\",\"supply_date\":\"4\",\"stockout_date\":\"2014-04-02\",\"online_date\":\"2014-02-27\",\"stocks\":\"-1\",\"warehouse\":\"CN\",\"add_date\":\"1397117942\",\"msg\":\"Usually dispatched in 6-9 business days\",\"id\":\"450329\",\"supply_id\":\"0\",\"supply_name\":\"\",\"products_url\":\"\",\"products_center_id\":\"7\",\"only_web\":\"1\",\"shipDays\":\"9\",\"chinastocks\":\"-1\",\"expected_arrivaltimes\":\"0000-00-00\",\"is_approved\":\"1\",\"order_type\":\"0\",\"is_unpurchase\":\"0\",\"shelf_area\":\"X\\u533a\",\"shelf_row\":\"N\\u6392\",\"area_number\":\"0\",\"allot_stocks\":\"0\",\"allot_arrival_time\":\"0000-00-00\",\"alreadystocks\":\"1\",\"msg_source\":\"table_expire_0\",\"no_stock_wh_poa\":{\"POA184066\":\"-2\",\"POA184065\":\"-1\"},\"wh_to_countries\":\"\"}");
		assertEquals("POA184065", stringStringMap.get("sku_or_poa"));
		assertEquals("916095", stringStringMap.get("products_id"));
		assertEquals("11", stringStringMap.get("quantity_status"));
		assertEquals("4", stringStringMap.get("supply_date"));
		assertEquals("2014-04-02", stringStringMap.get("stockout_date"));
		assertEquals("2014-02-27", stringStringMap.get("online_date"));
		assertEquals("-1", stringStringMap.get("stocks"));
		assertEquals("9", stringStringMap.get("shipDays"));
	}

	@Test
	public void testSupplierDescription1() throws Exception {
		final BanggoodMobileDataLoader dataLoader = new BanggoodMobileDataLoader();
		dataLoader.afterPropertiesSet();

		final HibernateSupplierInfo info1 = new HibernateSupplierInfo("/DJI-Phantom-FC40-RC-Quadcopter-With-Wi-Fi-FPV-FC40-Camera-p-916095.html", "SKU118444", Supplier.BANGGOOD, null);
		final SupplierDescription desc1 = dataLoader.loadDescription(info1);
		assertNotNull(desc1);
		assertNotNull(desc1.getPrice());
		System.out.println(desc1);
	}

	@Test
	public void testSupplierDescription2() throws Exception {
		final BanggoodMobileDataLoader dataLoader = new BanggoodMobileDataLoader();
		dataLoader.afterPropertiesSet();

		final HibernateSupplierInfo info2 = new HibernateSupplierInfo("/Wholesale-Replacement-WLtoys-V911-2_4GHz-4CH-RC-Helicopter-BNF-New-Plug-Version-p-39473.html", "SKU043151", Supplier.BANGGOOD, null);
		final SupplierDescription desc2 = dataLoader.loadDescription(info2);
		assertNotNull(desc2);
		assertNotNull(desc2.getPrice());
		System.out.println(desc2);
	}

	@Test
	public void testSupplierDescription3() throws Exception {
		final BanggoodMobileDataLoader dataLoader = new BanggoodMobileDataLoader();
		dataLoader.afterPropertiesSet();

		final HibernateSupplierInfo info2 = new HibernateSupplierInfo("/p-46246.html", "SKU105465", Supplier.BANGGOOD, null);
		final SupplierDescription desc2 = dataLoader.loadDescription(info2);
		assertNotNull(desc2);
		assertNotNull(desc2.getPrice());
		System.out.println(desc2);
	}
}
