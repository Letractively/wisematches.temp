package billiongoods.server.services.coupon.impl;

import billiongoods.server.services.coupon.CouponType;
import billiongoods.server.services.coupon.ReferenceType;
import billiongoods.server.warehouse.Catalog;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.Price;
import billiongoods.server.warehouse.Product;
import org.junit.Test;

import java.util.Date;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateCouponTest {
	public HibernateCouponTest() {
	}

	@Test
	public void test_isActive() throws InterruptedException {
		final HibernateCoupon c1 = new HibernateCoupon("MOCK1", 12.3d, CouponType.FIXED, 10, ReferenceType.CATEGORY, 100);
		assertTrue(c1.isActive());
		c1.usedCoupons(99);
		assertTrue(c1.isActive());
		c1.usedCoupons(1);
		assertFalse(c1.isActive());

		final HibernateCoupon c2 = new HibernateCoupon("MOCK1", 12.3d, CouponType.FIXED, 10, ReferenceType.CATEGORY, new Date(System.currentTimeMillis() + 100), null);
		assertFalse(c2.isActive());
		Thread.sleep(102);
		assertTrue(c2.isActive());

		final HibernateCoupon c3 = new HibernateCoupon("MOCK1", 12.3d, CouponType.FIXED, 10, ReferenceType.CATEGORY, null, new Date(System.currentTimeMillis() + 100));
		assertTrue(c3.isActive());
		Thread.sleep(102);
		assertFalse(c3.isActive());

		final HibernateCoupon c4 = new HibernateCoupon("MOCK1", 12.3d, CouponType.FIXED, 10, ReferenceType.CATEGORY, null, null, 0);
		assertTrue(c4.isActive());
		c4.close();
		assertFalse(c4.isActive());
	}

	@Test
	public void test_isApplicable() {
		final Category c1 = createMock(Category.class);
		final Category c2 = createMock(Category.class);

		expect(c1.isRealKinship(c1)).andReturn(true).anyTimes();
		expect(c1.isRealKinship(c2)).andReturn(true).anyTimes();
		expect(c2.isRealKinship(c1)).andReturn(false).anyTimes();
		expect(c2.isRealKinship(c2)).andReturn(true).anyTimes();
		replay(c1, c2);

		final Product product = createMock(Product.class);
		expect(product.getId()).andReturn(1).andReturn(1);
		expect(product.getCategoryId()).andReturn(2).andReturn(2).andReturn(1).andReturn(1);
		replay(product);

		final Catalog catalog = createMock(Catalog.class);
		expect(catalog.getCategory(1)).andReturn(c1).anyTimes();
		expect(catalog.getCategory(2)).andReturn(c2).anyTimes();
		replay(catalog);

		assertTrue(new HibernateCoupon("MOCK1", 12.3d, CouponType.FIXED, 1, ReferenceType.PRODUCT, 100).isApplicable(product, catalog));
		assertFalse(new HibernateCoupon("MOCK1", 12.3d, CouponType.FIXED, 2, ReferenceType.PRODUCT, 100).isApplicable(product, catalog));

		assertTrue(new HibernateCoupon("MOCK1", 12.3d, CouponType.FIXED, 1, ReferenceType.CATEGORY, 100).isApplicable(product, catalog));
		assertTrue(new HibernateCoupon("MOCK1", 12.3d, CouponType.FIXED, 2, ReferenceType.CATEGORY, 100).isApplicable(product, catalog));

		assertTrue(new HibernateCoupon("MOCK1", 12.3d, CouponType.FIXED, 1, ReferenceType.CATEGORY, 100).isApplicable(product, catalog));
		assertFalse(new HibernateCoupon("MOCK1", 12.3d, CouponType.FIXED, 2, ReferenceType.CATEGORY, 100).isApplicable(product, catalog));
	}

	@Test
	public void test_process() {
		final Product product = createMock(Product.class);
		expect(product.getId()).andReturn(1).anyTimes();
		expect(product.getPrice()).andReturn(new Price(100.d)).anyTimes();
		replay(product);

		assertEquals(80.d, new HibernateCoupon("MOCK1", 20.d, CouponType.FIXED, 1, ReferenceType.PRODUCT, 100).process(product, null), 0.00000001);
		assertEquals(0.d, new HibernateCoupon("MOCK1", 200.d, CouponType.FIXED, 1, ReferenceType.PRODUCT, 100).process(product, null), 0.00000001);
		assertEquals(20.d, new HibernateCoupon("MOCK1", 20.d, CouponType.PRICE, 1, ReferenceType.PRODUCT, 100).process(product, null), 0.00000001);
		assertEquals(100.d, new HibernateCoupon("MOCK1", 200.d, CouponType.PRICE, 1, ReferenceType.PRODUCT, 100).process(product, null), 0.00000001);
		assertEquals(80.d, new HibernateCoupon("MOCK1", 20.d, CouponType.PERCENT, 1, ReferenceType.PRODUCT, 100).process(product, null), 0.00000001);
	}
}
