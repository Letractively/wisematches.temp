package billiongoods.server.services.payment.impl;

import billiongoods.core.Personality;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.payment.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateOrderManager implements OrderManager {
	private SessionFactory sessionFactory;

	public HibernateOrderManager() {
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Order createOrder(Personality person, Basket basket, Address address, PaymentSystem system) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateOrder order = new HibernateOrder(person.getId(), null, new HibernateAddress(address));
		session.save(order);

		final List<OrderItem> items = new ArrayList<>();
		for (BasketItem basketItem : basket.getBasketItems()) {
			items.add(new HibernateOrderItem(order, basketItem));
		}
		order.setOrderItems(items);
		session.update(order);

		return order;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void deleteOrder(Long orderId) {
		throw new UnsupportedOperationException("TODO: Not implemented");
	}

	@Override
	public void changeState(Long orderId, OrderState state) {
		throw new UnsupportedOperationException("TODO: Not implemented");
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
