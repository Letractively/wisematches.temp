package billiongoods.server.services.payment.impl;

import billiongoods.core.Visitor;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.payment.*;
import billiongoods.server.warehouse.ArticleDescription;
import billiongoods.server.warehouse.Property;
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
		HibernateOrderManager orderManager = new HibernateOrderManager();
		orderManager.setSessionFactory(sessionFactory);

		final ArticleDescription desc = createMock(ArticleDescription.class);
		expect(desc.getName()).andReturn("Item1").andReturn("Item2");
		expect(desc.getCode()).andReturn("000001").andReturn("000002");
		expect(desc.getPrice()).andReturn(123.34f).andReturn(342.21f);
		expect(desc.getWeight()).andReturn(0.34f).andReturn(2.21f);
		replay(desc);

		final Property property = new Property(new HibernateAttribute("a1", "mock"), "AV");

		final BasketItem item1 = createMock(BasketItem.class);
		expect(item1.getNumber()).andReturn(0);
		expect(item1.getQuantity()).andReturn(12);
		expect(item1.getOptions()).andReturn(Collections.singletonList(property));
		expect(item1.getArticle()).andReturn(desc);
		replay(item1);

		final BasketItem item2 = createMock(BasketItem.class);
		expect(item2.getNumber()).andReturn(7);
		expect(item2.getQuantity()).andReturn(3);
		expect(item2.getOptions()).andReturn(null);
		expect(item2.getArticle()).andReturn(desc);
		replay(item2);

		final Basket basket = createMock(Basket.class);
		expect(basket.getBasketItems()).andReturn(Arrays.asList(item1, item2));
		replay(basket);

		final HibernateAddress address = new HibernateAddress();
		address.setName("MockName");
		address.setPostalCode("123456");
		address.setCity("MockCity");
		address.setRegion("MockRegion");
		address.setStreetAddress("MockStreet, d.344/2 k.1, kv. 9881");

		final Order order = orderManager.createOrder(new Visitor(123L), basket, address, PaymentSystem.PAY_PAL);
		assertNotNull(order);
		System.out.println(order);

		assertNotNull(order.getId());
		assertEquals(123, order.getBuyer().longValue());
		assertEquals(OrderState.NEW, order.getOrderState());

		final List<OrderItem> orderItems = order.getOrderItems();
		assertEquals(2, orderItems.size());

		final Address address1 = order.getAddress();
		assertEquals("MockName", address1.getName());
		assertEquals("123456", address1.getPostalCode());
		assertEquals("MockCity", address1.getCity());
		assertEquals("MockRegion", address1.getRegion());
		assertEquals("MockStreet, d.344/2 k.1, kv. 9881", address1.getStreetAddress());

		final OrderItem oi0 = orderItems.get(0);
		assertEquals(0, oi0.getNumber().intValue());
		assertEquals(12, oi0.getQuantity());
		assertEquals("Item1", oi0.getName());
		assertEquals("000001", oi0.getCode());
		assertEquals(0.34f, oi0.getWeight(), 0.0000f);
		assertEquals(123.34f, oi0.getAmount(), 0.0000f);

		final OrderItem oi1 = orderItems.get(1);
		assertEquals(7, oi1.getNumber().intValue());
		assertEquals(3, oi1.getQuantity());
		assertEquals("Item2", oi1.getName());
		assertEquals("000002", oi1.getCode());
		assertEquals(2.21f, oi1.getWeight(), 0.0000f);
		assertEquals(342.21f, oi1.getAmount(), 0.0000f);

		verify(desc);
	}
}
