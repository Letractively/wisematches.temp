package billiongoods.server.services.payment.impl;

import billiongoods.core.Personality;
import billiongoods.server.services.basket.Basket;
import billiongoods.server.services.basket.BasketItem;
import billiongoods.server.services.payment.*;
import org.hibernate.Query;
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
        final Session session = sessionFactory.getCurrentSession();
        final HibernateOrder order = (HibernateOrder) session.get(HibernateOrder.class, orderId);
        if (order != null) {
            session.delete(order);
        }
    }

    @Override
    public void changeState(Long orderId, OrderState state) {
        final Session session = sessionFactory.getCurrentSession();

        final Query query = session.createQuery("update billiongoods.server.services.payment.impl.HibernateOrder set orderState=:state where id=:id");
        query.setParameter("id", orderId);
        query.setParameter("state", state);
        query.executeUpdate();
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
