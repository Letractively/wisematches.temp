package billiongoods.server.services.basket.impl;

import billiongoods.core.Visitor;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.warehouse.AttributeType;
import billiongoods.server.warehouse.ProductPreview;
import billiongoods.server.warehouse.Property;
import billiongoods.server.warehouse.impl.HibernateAttribute;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/config/properties-config.xml",
		"classpath:/config/database-config.xml"
})
public class HibernateBasketManagerTest {
	@Autowired
	private SessionFactory sessionFactory;

	public HibernateBasketManagerTest() {
	}

	@Test
	public void test() {
		final HibernateBasketManager manager = new HibernateBasketManager();
		manager.setSessionFactory(sessionFactory);

		final Visitor person = new Visitor(System.currentTimeMillis());

		final HibernateBasket basket = manager.getBasket(person);
		assertNull(basket);

		final ProductPreview product = createMock(ProductPreview.class);
		expect(product.getId()).andReturn(120).anyTimes();
		replay(product);

		final List<Property> props = new ArrayList<>();
		props.add(new Property(new HibernateAttribute("mock1", "mock", null, AttributeType.STRING), "v1"));
		props.add(new Property(new HibernateAttribute("mock2", "mock", null, AttributeType.STRING), "v2"));

		final BasketItem item1 = manager.addBasketItem(person, product, props, 10);
		assertEquals(0, item1.getNumber());

		final BasketItem item2 = manager.addBasketItem(person, product, props, 20);
		assertEquals(1, item2.getNumber());

		final BasketItem item3 = manager.addBasketItem(person, product, null, 30);
		assertEquals(2, item3.getNumber());

		final HibernateBasket basket1 = manager.getBasket(person);
		assertEquals(3, basket1.getBasketItems().size());
		assertEquals(7, basket1.getExpirationDays().intValue());

		manager.removeBasketItem(person, item2.getNumber());
		assertEquals(2, basket1.getBasketItems().size());

		final BasketItem item4 = manager.addBasketItem(person, product, null, 40);
		assertEquals(1, item4.getNumber());

		final BasketItem basketItem = manager.updateBasketItem(person, 1, 20);
		assertEquals(20, basketItem.getQuantity());

		manager.closeBasket(person);

		assertNull(manager.getBasket(person));
	}
}
