package billiongoods.server.services.coupon.impl;

import billiongoods.server.services.coupon.CouponAmountType;
import billiongoods.server.services.coupon.CouponReferenceType;
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
		final HibernateCoupon c1 = new HibernateCoupon("MOCK1", 12.3d, CouponAmountType.FIXED, 10, CouponReferenceType.CATEGORY, 2, null);
		assertTrue(c1.isActive());
		assertFalse(c1.isTerminated());
		assertFalse(c1.isFullyUtilized());
		assertEquals(0, c1.getUtilizedCount());
		assertEquals(2, c1.getAllocatedCount());

		c1.redeemCoupon();
		assertTrue(c1.isActive());
		assertFalse(c1.isTerminated());
		assertFalse(c1.isFullyUtilized());
		assertEquals(1, c1.getUtilizedCount());
		assertEquals(2, c1.getAllocatedCount());

		c1.redeemCoupon();
		assertFalse(c1.isActive());
		assertFalse(c1.isTerminated());
		assertTrue(c1.isFullyUtilized());
		assertEquals(2, c1.getUtilizedCount());
		assertEquals(2, c1.getAllocatedCount());

		final HibernateCoupon c2 = new HibernateCoupon("MOCK1", 12.3d, CouponAmountType.FIXED, 10, CouponReferenceType.CATEGORY, 0, new Date(System.currentTimeMillis() + 100));
		assertTrue(c2.isActive());
		Thread.sleep(102);
		assertFalse(c2.isActive());

		final HibernateCoupon c4 = new HibernateCoupon("MOCK1", 0, CouponAmountType.FIXED, 10, CouponReferenceType.CATEGORY, 0, null);
		assertTrue(c4.isActive());
		c4.close();
		Thread.sleep(102);
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

		assertTrue(new HibernateCoupon("MOCK1", 12.3d, CouponAmountType.FIXED, 1, CouponReferenceType.PRODUCT, 100, null).isApplicable(product, catalog));
		assertFalse(new HibernateCoupon("MOCK1", 12.3d, CouponAmountType.FIXED, 2, CouponReferenceType.PRODUCT, 100, null).isApplicable(product, catalog));

		assertTrue(new HibernateCoupon("MOCK1", 12.3d, CouponAmountType.FIXED, 1, CouponReferenceType.CATEGORY, 100, null).isApplicable(product, catalog));
		assertTrue(new HibernateCoupon("MOCK1", 12.3d, CouponAmountType.FIXED, 2, CouponReferenceType.CATEGORY, 100, null).isApplicable(product, catalog));

		assertTrue(new HibernateCoupon("MOCK1", 12.3d, CouponAmountType.FIXED, 1, CouponReferenceType.CATEGORY, 100, null).isApplicable(product, catalog));
		assertFalse(new HibernateCoupon("MOCK1", 12.3d, CouponAmountType.FIXED, 2, CouponReferenceType.CATEGORY, 100, null).isApplicable(product, catalog));
	}

	@Test
	public void test_getDiscount_Product() {
		final Product product = createMock(Product.class);
		expect(product.getId()).andReturn(1).anyTimes();
		expect(product.getPrice()).andReturn(new Price(100.d)).anyTimes();
		replay(product);

		assertEquals(20.d, new HibernateCoupon("MOCK1", 20.d, CouponAmountType.FIXED, 1, CouponReferenceType.PRODUCT, 100, null).getDiscount(product, null), 0.00000001);
		assertEquals(0.d, new HibernateCoupon("MOCK1", 200.d, CouponAmountType.FIXED, 1, CouponReferenceType.PRODUCT, 100, null).getDiscount(product, null), 0.00000001);
		assertEquals(80.d, new HibernateCoupon("MOCK1", 20.d, CouponAmountType.PRICE, 1, CouponReferenceType.PRODUCT, 100, null).getDiscount(product, null), 0.00000001);
		assertEquals(0.d, new HibernateCoupon("MOCK1", 200.d, CouponAmountType.PRICE, 1, CouponReferenceType.PRODUCT, 100, null).getDiscount(product, null), 0.00000001);
		assertEquals(20.d, new HibernateCoupon("MOCK1", 20.d, CouponAmountType.PERCENT, 1, CouponReferenceType.PRODUCT, 100, null).getDiscount(product, null), 0.00000001);
	}
}
