package billiongoods.server.services.payment.impl;

import billiongoods.core.Visitor;
import billiongoods.server.services.address.Address;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.coupon.CouponManager;
import billiongoods.server.services.payment.*;
import billiongoods.server.warehouse.*;
import billiongoods.server.warehouse.impl.HibernateAttribute;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/properties-config.xml",
		"classpath:/config/database-config.xml"
})
public class HibernateOrderManagerTest {
	@Autowired
	private SessionFactory sessionFactory;

	public HibernateOrderManagerTest() {
	}

	@Test
	public void test() {
		final CategoryManager categoryManager = createMock(CategoryManager.class);

		final CouponManager couponManager = createMock(CouponManager.class);
		expect(couponManager.getCoupon(null)).andReturn(null);
		replay(couponManager);

		final HibernateOrderManager orderManager = new HibernateOrderManager();
		orderManager.setSessionFactory(sessionFactory);
		orderManager.setShipmentManager(new DefaultShipmentManager());
		orderManager.setCouponManager(couponManager);
		orderManager.setCategoryManager(categoryManager);

		final ProductPreview desc = createMock(ProductPreview.class);
		expect(desc.getPrice()).andReturn(new Price(123.34d)).andReturn(new Price(342.21d));
		expect(desc.getWeight()).andReturn(0.34d).andReturn(2.21d);
		replay(desc);

		final Property property = new Property(new HibernateAttribute("a1", "mock", null, AttributeType.STRING), "AV");

		final BasketItem item1 = createMock(BasketItem.class);
		expect(item1.getNumber()).andReturn(0);
		expect(item1.getQuantity()).andReturn(12);
		expect(item1.getOptions()).andReturn(Collections.singletonList(property));
		expect(item1.getProduct()).andReturn(desc);
		replay(item1);

		final BasketItem item2 = createMock(BasketItem.class);
		expect(item2.getNumber()).andReturn(7);
		expect(item2.getQuantity()).andReturn(3);
		expect(item2.getOptions()).andReturn(null);
		expect(item2.getProduct()).andReturn(desc);
		replay(item2);

		final Basket basket = createMock(Basket.class);
		expect(basket.getAmount()).andReturn(23.9d).times(2);
		expect(basket.getWeight()).andReturn(23.9d).times(2);
		expect(basket.getBasketItems()).andReturn(Arrays.asList(item1, item2));
		expect(basket.getCoupon()).andReturn(null);
		replay(basket);

		final Address address = new Address("Mock", "Name", "+7-912-232-12-45", "123456", "MockRegion", "MockCity", "MockStreet, d.344/2 k.1, kv. 9881");
		Order order = orderManager.create(new Visitor(123L), basket, address, ShipmentType.REGISTERED, true);

		final Shipment shipment = order.getShipment();
		assertNotNull(order.getId());
		assertEquals(23.9d, order.getAmount(), 0.0000001d);
		assertEquals(70d, shipment.getAmount(), 0.0000001d);
		assertEquals(ShipmentType.REGISTERED, shipment.getType());
		assertEquals(123, order.getPersonId().longValue());
		assertEquals(OrderState.NEW, order.getOrderState());

		final List<OrderItem> orderItems = order.getOrderItems();
		assertEquals(2, orderItems.size());

		final Address address1 = shipment.getAddress();
		assertEquals("Mock", address1.getFirstName());
		assertEquals("Name", address1.getLastName());
		assertEquals("123456", address1.getPostcode());
		assertEquals("MockCity", address1.getCity());
		assertEquals("MockRegion", address1.getRegion());
		assertEquals("MockStreet, d.344/2 k.1, kv. 9881", address1.getLocation());

		final OrderItem oi0 = orderItems.get(0);
		assertNotNull(oi0.getProduct());
		assertEquals(12, oi0.getQuantity());
		assertEquals(0.34d, oi0.getWeight(), 0.0000d);
		assertEquals(123.34d, oi0.getAmount(), 0.0000d);

		final OrderItem oi1 = orderItems.get(1);
		assertNotNull(oi1.getProduct());
		assertEquals(3, oi1.getQuantity());
		assertEquals(2.21d, oi1.getWeight(), 0.0000d);
		assertEquals(342.21d, oi1.getAmount(), 0.0000d);

		order = orderManager.getOrder(order.getId());
		orderManager.bill(order.getId(), "1234567890987654321");
		assertEquals("1234567890987654321", order.getToken());
		assertEquals(OrderState.BILLING, order.getOrderState());

		order = orderManager.getOrder(order.getId());
		orderManager.accept(order.getId(), "mock1@mock.mock", "Mock Chmock", "my note", "ASDAWEQWEASD");
		assertEquals("mock1@mock.mock", order.getPayer());
		assertEquals("Mock Chmock", order.getPayerName());
		assertEquals(OrderState.ACCEPTED, order.getOrderState());

		order = orderManager.getOrder(order.getId());
		orderManager.processing(order.getId(), "124343", "Comment1");
		assertEquals("124343", order.getReferenceTracking());
		assertEquals("Comment1", order.getCommentary());
		assertEquals(OrderState.PROCESSING, order.getOrderState());

		order = orderManager.getOrder(order.getId());
		orderManager.shipping(order.getId(), "6564564", "Comment2");
		assertEquals("6564564", order.getChinaMailTracking());
		assertEquals("Comment2", order.getCommentary());
		assertEquals(OrderState.SHIPPING, order.getOrderState());

		order = orderManager.getOrder(order.getId());
		orderManager.shipped(order.getId(), "EW32143523TR", "Comment3");
		assertEquals("Comment3", order.getCommentary());
		assertEquals("EW32143523TR", order.getInternationalTracking());
		assertEquals(OrderState.SHIPPED, order.getOrderState());

		order = orderManager.getOrder(order.getId());
		orderManager.failed(order.getId(), "They, close");
		assertEquals("They, close", order.getCommentary());
		assertEquals(OrderState.FAILED, order.getOrderState());

		order = orderManager.getOrder(order.getId());
		orderManager.cancel(order.getId(), "ASASDASDASD23123", "my note");
		assertEquals("ASASDASDASD23123", order.getRefundToken());
		assertEquals(OrderState.CANCELLED, order.getOrderState());

		assertEquals(7, order.getOrderLogs().size());

		verify(desc);
	}
}
