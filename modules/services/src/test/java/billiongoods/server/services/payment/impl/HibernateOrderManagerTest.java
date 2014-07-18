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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * TODO: add listeners testing here!
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/properties-config.xml",
		"classpath:/config/database-config.xml"
})
public class HibernateOrderManagerTest {
	private Order order;

	private final HibernateOrderManager orderManager = new HibernateOrderManager();
	private final DefaultShipmentManager shipmentManager = new DefaultShipmentManager();

	private final Address address = new Address("Mock", "Name", "+7-912-232-12-45", "123456", "MockRegion", "MockCity", "MockStreet, d.344/2 k.1, kv. 9881");

	@Autowired
	private SessionFactory sessionFactory;

	public HibernateOrderManagerTest() {
	}

	@Before
	public void setUp() throws Exception {
		final CategoryManager categoryManager = createMock(CategoryManager.class);

		final CouponManager couponManager = createMock(CouponManager.class);
		expect(couponManager.getCoupon(null)).andReturn(null);
		replay(couponManager);

		orderManager.setSessionFactory(sessionFactory);
		orderManager.setShipmentManager(shipmentManager);
		orderManager.setCouponManager(couponManager);
		orderManager.setCategoryManager(categoryManager);

		final ProductPreview desc1 = createMock(ProductPreview.class);
		expect(desc1.getId()).andReturn(102).anyTimes();
		expect(desc1.getPrice()).andReturn(new Price(123.34d)).anyTimes();
		expect(desc1.getWeight()).andReturn(0.34d).anyTimes();
		replay(desc1);

		final ProductPreview desc2 = createMock(ProductPreview.class);
		expect(desc2.getId()).andReturn(103).anyTimes();
		expect(desc2.getPrice()).andReturn(new Price(342.21d)).anyTimes();
		expect(desc2.getWeight()).andReturn(2.21d).anyTimes();
		replay(desc2);

		final Property property = new Property(new HibernateAttribute("a1", "mock", null, AttributeType.STRING), "AV");

		final BasketItem item1 = createMock(BasketItem.class);
		expect(item1.getNumber()).andReturn(0);
		expect(item1.getQuantity()).andReturn(12);
		expect(item1.getOptions()).andReturn(Collections.singletonList(property));
		expect(item1.getProduct()).andReturn(desc1);
		replay(item1);

		final BasketItem item2 = createMock(BasketItem.class);
		expect(item2.getNumber()).andReturn(7);
		expect(item2.getQuantity()).andReturn(3);
		expect(item2.getOptions()).andReturn(null);
		expect(item2.getProduct()).andReturn(desc2);
		replay(item2);

		final Basket basket = createMock(Basket.class);
		expect(basket.getAmount()).andReturn(23.9d).times(2);
		expect(basket.getWeight()).andReturn(23.9d).times(2);
		expect(basket.getBasketItems()).andReturn(Arrays.asList(item1, item2));
		expect(basket.getCoupon()).andReturn(null);
		replay(basket);

		order = orderManager.create(new Visitor(123L), basket, address, ShipmentType.REGISTERED);
	}

	@Test
	public void testOrder() {
		assertNull(order.getTimeline().getStarted());
		assertNotNull(order.getTimeline().getCreated());

		final Shipment shipment = order.getShipment();
		assertNotNull(order.getId());
		assertEquals(23.9d, order.getAmount(), 0.0000001d);
		assertEquals(70d, shipment.getAmount(), 0.0000001d);
		assertEquals(ShipmentType.REGISTERED, shipment.getType());
		assertEquals(123, order.getPersonId().longValue());
		assertEquals(OrderState.NEW, order.getState());

		final Address address1 = shipment.getAddress();
		assertEquals("Mock", address1.getFirstName());
		assertEquals("Name", address1.getLastName());
		assertEquals("Mock Name", address1.getFullName());
		assertEquals("123456", address1.getPostcode());
		assertEquals("MockCity", address1.getCity());
		assertEquals("MockRegion", address1.getRegion());
		assertEquals("MockStreet, d.344/2 k.1, kv. 9881", address1.getLocation());

		final List<OrderItem> orderItems = order.getItems();
		assertEquals(2, orderItems.size());

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
	}

	@Test
	public void testBilling() {
		order = orderManager.bill(order.getId(), "1234567890987654321");
		assertNull(order.getTimeline().getStarted());
		assertEquals("1234567890987654321", order.getPayment().getToken());
		assertEquals(OrderState.BILLING, order.getState());
	}

	@Test
	public void testAccepted() {
		testBilling();

		order = orderManager.accept(order.getId(), "ASDAWEQWEASD", 123.45, "mock1@mock.mock", "Mock Chmock", "my note");
		assertNotNull(order.getTimeline().getStarted());

		final OrderPayment payment = order.getPayment();
		assertEquals("mock1@mock.mock", payment.getPayer());
		assertEquals("1234567890987654321", payment.getToken());
		assertEquals("Mock Chmock", payment.getPayerName());
		assertEquals("my note", payment.getPayerNote());
		assertEquals("ASDAWEQWEASD", payment.getPaymentId());
		assertEquals(Double.valueOf(123.45), payment.getPaymentAmount());
		assertEquals(OrderState.ACCEPTED, order.getState());
	}

	@Test
	public void testRejected() {
		testBilling();

		order = orderManager.reject(order.getId(), "ASDAWEQWEASD", 123.45, "mock1@mock.mock", "Mock Chmock", "my note");
		assertNotNull(order.getTimeline().getFinished());

		final OrderPayment payment = order.getPayment();
		assertEquals("mock1@mock.mock", payment.getPayer());
		assertEquals("1234567890987654321", payment.getToken());
		assertEquals("Mock Chmock", payment.getPayerName());
		assertEquals("my note", payment.getPayerNote());
		assertEquals("ASDAWEQWEASD", payment.getPaymentId());
		assertEquals(Double.valueOf(123.45), payment.getPaymentAmount());
		assertEquals(OrderState.CANCELLED, order.getState());

	}

	@Test
	public void testFailed() {
		testBilling();

		order = orderManager.failed(order.getId(), "Mock reason");
		assertNotNull(order.getTimeline().getFinished());
		assertEquals(OrderState.FAILED, order.getState());
		assertEquals("Mock reason", order.getCommentary());
	}

	@Test
	public void testCancel() {
		testBilling();

		order = orderManager.cancel(order.getId(), "Mock reason");
		assertNotNull(order.getTimeline().getFinished());
		assertEquals(OrderState.CANCELLED, order.getState());
		assertEquals("Mock reason", order.getCommentary());
	}

	@Test
	public void testSuspend() {
		testBilling();

		order = orderManager.suspend(order.getId(), "Mock reason");
		assertEquals(OrderState.SUSPENDED, order.getState());
		assertEquals("Mock reason", order.getCommentary());
	}

	@Test
	public void testProcessing() {
		testAccepted();

		try {
			order = orderManager.process(order.getId(), new ParcelEntry(12, 0));
			fail("Exception must be here");
		} catch (IllegalArgumentException ignore) {
		}

		try {
			order = orderManager.process(order.getId(), new ParcelEntry(12, 0, 1, 2));
			fail("Exception must be here");
		} catch (IllegalArgumentException ignore) {
		}

		try {
			order = orderManager.process(order.getId(), new ParcelEntry(12, 0, 1), new ParcelEntry(13, 2));
			fail("Exception must be here");
		} catch (IllegalArgumentException ignore) {
		}

		order = orderManager.process(order.getId(), new ParcelEntry(12, 0), new ParcelEntry(13, 1));
		assertNotNull(order.getTimeline().getProcessed());
		assertEquals(OrderState.PROCESSING, order.getState());

		final List<Parcel> parcels = order.getParcels();
		assertEquals(2, parcels.size());

		final Parcel p1 = parcels.get(0);
		assertEquals(12, p1.getNumber());
		assertEquals(ParcelState.PROCESSING, p1.getState());
		assertTrue(p1.contains(order.getItems().get(0)));
		assertEquals(1, order.getItems(p1).size());
		assertEquals(order.getTimeline().getCreated(), p1.getTimeline().getCreated());
		assertEquals(order.getTimeline().getStarted(), p1.getTimeline().getStarted());
		assertEquals(Integer.valueOf(102), order.getItems(p1).get(0).getProduct().getId());
		assertNotNull(p1.getTimeline().getProcessed());

		final Parcel p2 = parcels.get(1);
		assertEquals(13, p2.getNumber());
		assertEquals(ParcelState.PROCESSING, p2.getState());
		assertTrue(p2.contains(order.getItems().get(1)));
		assertEquals(1, order.getItems(p2).size());
		assertEquals(order.getTimeline().getCreated(), p2.getTimeline().getCreated());
		assertEquals(order.getTimeline().getStarted(), p2.getTimeline().getStarted());
		assertEquals(Integer.valueOf(103), order.getItems(p2).get(0).getProduct().getId());
		assertNotNull(p2.getTimeline().getProcessed());
	}


	@Test
	public void testCancelWithParcels() {
		testProcessing();

		order = orderManager.cancel(order.getId(), "Mock reason");
		assertNotNull(order.getTimeline().getFinished());
		assertEquals(OrderState.CANCELLED, order.getState());
		assertEquals("Mock reason", order.getCommentary());

		final List<Parcel> parcels = order.getParcels();
		for (Parcel parcel : parcels) {
			assertEquals(ParcelState.CANCELLED, parcel.getState());
		}
	}

	@Test
	public void testShippingParcel() {
		testProcessing();

		final List<Parcel> parcels = order.getParcels();

		order = orderManager.shipping(order.getId(), parcels.get(0).getId(), "SHIPPING_TRACKING1", "My note1");
		assertNull(order.getTimeline().getShipped());
		assertNotNull(order.getTimeline().getProcessed());
		assertEquals(OrderState.PROCESSING, order.getState());

		assertEquals("My note1", order.getCommentary());
		assertEquals("SHIPPING_TRACKING1", order.getParcels().get(0).getChinaMailTracking());
		assertEquals(ParcelState.SHIPPING, order.getParcels().get(0).getState());
		assertNotNull(parcels.get(0).getTimeline().getShipped());

		order = orderManager.shipping(order.getId(), parcels.get(1).getId(), "SHIPPING_TRACKING2", "My note2");
		assertNotNull(order.getTimeline().getShipped());
		assertNotNull(order.getTimeline().getProcessed());
		assertEquals(OrderState.SHIPPING, order.getState());

		assertEquals("My note2", order.getCommentary());
		assertEquals("SHIPPING_TRACKING2", order.getParcels().get(1).getChinaMailTracking());
		assertEquals(ParcelState.SHIPPING, order.getParcels().get(1).getState());
		assertNotNull(parcels.get(1).getTimeline().getShipped());
	}

	@Test
	public void testShippedParcel() {
		testProcessing();

		final List<Parcel> parcels = order.getParcels();

		order = orderManager.shipped(order.getId(), parcels.get(0).getId(), "SHIPPED_TRACKING1", "My note1");
		assertNull(order.getTimeline().getShipped());
		assertNotNull(order.getTimeline().getProcessed());
		assertEquals(OrderState.PROCESSING, order.getState());

		assertEquals("My note1", order.getCommentary());
		assertEquals("SHIPPED_TRACKING1", order.getParcels().get(0).getInternationalTracking());
		assertEquals(ParcelState.SHIPPED, order.getParcels().get(0).getState());
		assertNotNull(parcels.get(0).getTimeline().getShipped());

		order = orderManager.shipped(order.getId(), parcels.get(1).getId(), "SHIPPED_TRACKING2", "My note2");
		assertNotNull(order.getTimeline().getShipped());
		assertNotNull(order.getTimeline().getProcessed());
		assertEquals(OrderState.SHIPPED, order.getState());

		assertEquals("My note2", order.getCommentary());
		assertEquals("SHIPPED_TRACKING2", order.getParcels().get(1).getInternationalTracking());
		assertEquals(ParcelState.SHIPPED, order.getParcels().get(1).getState());
		assertNotNull(parcels.get(1).getTimeline().getShipped());
	}

	@Test
	public void testSuspendParcel() {
		testProcessing();

		final List<Parcel> parcels = order.getParcels();

		order = orderManager.suspend(order.getId(), parcels.get(0).getId(), LocalDateTime.now(), "My note1");
		assertNull(order.getTimeline().getShipped());
		assertNotNull(order.getTimeline().getProcessed());
		assertEquals(OrderState.PROCESSING, order.getState());

		assertEquals("My note1", order.getCommentary());
		assertEquals(ParcelState.SUSPENDED, order.getParcels().get(0).getState());

		order = orderManager.suspend(order.getId(), parcels.get(1).getId(), LocalDateTime.now(), "My note2");
		assertNotNull(order.getTimeline().getProcessed());
		assertEquals(OrderState.PROCESSING, order.getState());

		assertEquals("My note2", order.getCommentary());
		assertEquals(ParcelState.SUSPENDED, order.getParcels().get(1).getState());
	}

	@Test
	public void testCancelledParcel() {
		testProcessing();

		final List<Parcel> parcels = order.getParcels();

		order = orderManager.cancel(order.getId(), parcels.get(0).getId(), "My note1");
		assertNull(order.getTimeline().getFinished());
		assertNotNull(order.getTimeline().getProcessed());
		assertEquals(OrderState.PROCESSING, order.getState());

		assertEquals("My note1", order.getCommentary());
		assertEquals(ParcelState.CANCELLED, order.getParcels().get(0).getState());
		assertNotNull(parcels.get(0).getTimeline().getFinished());

		order = orderManager.cancel(order.getId(), parcels.get(1).getId(), "My note2");
		assertNotNull(order.getTimeline().getFinished());
		assertNotNull(order.getTimeline().getProcessed());
		assertEquals(OrderState.CANCELLED, order.getState());

		assertEquals("My note2", order.getCommentary());
		assertEquals(ParcelState.CANCELLED, order.getParcels().get(1).getState());
		assertNotNull(parcels.get(1).getTimeline().getFinished());
	}

	@Test
	public void testCloseParcel() {
		testProcessing();

		final List<Parcel> parcels = order.getParcels();

		order = orderManager.close(order.getId(), parcels.get(0).getId(), LocalDateTime.now(), "My note1");
		assertNull(order.getTimeline().getFinished());
		assertNotNull(order.getTimeline().getProcessed());
		assertEquals(OrderState.PROCESSING, order.getState());

		assertEquals("My note1", order.getCommentary());
		assertEquals(ParcelState.CLOSED, order.getParcels().get(0).getState());
		assertNotNull(parcels.get(0).getTimeline().getFinished());

		order = orderManager.close(order.getId(), parcels.get(1).getId(), LocalDateTime.now(), "My note2");
		assertNotNull(order.getTimeline().getFinished());
		assertNotNull(order.getTimeline().getProcessed());
		assertEquals(OrderState.CLOSED, order.getState());

		assertEquals("My note2", order.getCommentary());
		assertEquals(ParcelState.CLOSED, order.getParcels().get(1).getState());
		assertNotNull(parcels.get(1).getTimeline().getFinished());
	}
}
