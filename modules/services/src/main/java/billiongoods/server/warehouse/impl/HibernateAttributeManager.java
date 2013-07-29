package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Attribute;
import billiongoods.server.warehouse.AttributeManager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateAttributeManager implements AttributeManager, InitializingBean {
    private SessionFactory sessionFactory;
    private final Map<Integer, Attribute> attributeMap = new HashMap<>();

    public HibernateAttributeManager() {
    }

    public void invalidate() {
        attributeMap.clear();

        final Session session = sessionFactory.openSession();
        final Query query = session.createQuery("from billiongoods.server.warehouse.impl.HibernateAttribute");

        final List list = query.list();
        for (Object o : list) {
            final HibernateAttribute a = (HibernateAttribute) o;
            attributeMap.put(a.getId(), a);
            session.evict(a);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        invalidate();
    }

    @Override
    public Attribute getAttribute(Integer id) {
        return attributeMap.get(id);
    }

    @Override
    public Collection<Attribute> getAttributes() {
        return attributeMap.values();
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Attribute addAttribute(String name, String unit) {
        final Attribute attribute = getAttribute(name);
        if (attribute != null) {
            return attribute;
        }
        final Session session = sessionFactory.getCurrentSession();
        final HibernateAttribute a = new HibernateAttribute(name, unit);
        session.save(a);
        session.evict(a);
        attributeMap.put(a.getId(), a);
        return a;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Attribute removeAttribute(Integer id) {
        final Attribute attribute = attributeMap.remove(id);
        if (attribute != null) {
            final Session session = sessionFactory.getCurrentSession();
            final Query query = session.createQuery("delete from billiongoods.server.warehouse.impl.HibernateAttribute a where a.id=:id");
            query.setInteger("id", id);
            query.executeUpdate();
        }
        return attribute;
    }

    private Attribute getAttribute(String name) {
        for (Attribute attribute : attributeMap.values()) {
            if (attribute.getName().equalsIgnoreCase(name)) {
                return attribute;
            }
        }
        return null;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
