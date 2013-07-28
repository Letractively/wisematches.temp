package billiongoods.server.warehouse.impl;

import billiongoods.core.search.entity.EntitySearchManager;
import billiongoods.server.warehouse.Attribute;
import billiongoods.server.warehouse.AttributeManager;
import org.hibernate.Criteria;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateAttributeManager extends EntitySearchManager<Attribute, Void> implements AttributeManager {
    public HibernateAttributeManager() {
        super(HibernateAttribute.class);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Attribute getAttribute(Integer id) {
        return (Attribute) sessionFactory.getCurrentSession().get(HibernateAttribute.class, id);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Attribute addAttribute(String name, String unit) {
        final HibernateAttribute a = new HibernateAttribute(name, unit);
        sessionFactory.getCurrentSession().save(a);
        return a;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void removeAttribute(Attribute attribute) {
        sessionFactory.getCurrentSession().delete(attribute);
    }

    @Override
    protected void applyRestrictions(Criteria criteria, Void context) {
    }

    @Override
    protected void applyProjections(Criteria criteria, Void context) {
    }
}
