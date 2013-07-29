package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateCategoryManager implements CategoryManager, InitializingBean {
    private SessionFactory sessionFactory;
    private AttributeManager attributeManager;

    private final DefaultCatalog catalog = new DefaultCatalog();
    private final Map<Integer, HibernateCategory> categoryMap = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.HibernateCategoryManager");

    public HibernateCategoryManager() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception {
        final Session session = sessionFactory.openSession();
        final Query query = session.createQuery("from billiongoods.server.warehouse.impl.HibernateCategory");

        final List list = query.list();
        for (Object o : list) {
            final HibernateCategory category = (HibernateCategory) o;
            categoryMap.put(category.getId(), category);
            session.evict(category);
        }

        for (HibernateCategory category : categoryMap.values()) {
            category.preInit(categoryMap.get(category.getParentId()));
        }

        final List<Category> rootCategories = new ArrayList<>();
        for (HibernateCategory category : categoryMap.values()) {
            category.initialize(attributeManager);

            if (category.getParent() == null) {
                rootCategories.add(category);
            }
        }
        Collections.sort(rootCategories, HibernateCategory.COMPARATOR);
        catalog.setRootCategories(rootCategories);

        log.info("Found {} categories", categoryMap.size());
    }

    @Override
    public Catalog getCatalog() {
        return catalog;
    }

    @Override
    public Category getCategory(Integer id) {
        return categoryMap.get(id);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Category addCategory(String name, String description, Set<Attribute> attributes, Category parent) {
        final Session session = sessionFactory.getCurrentSession();

        final HibernateCategory p = (HibernateCategory) parent;

        final HibernateCategory i = new HibernateCategory(name, description, p);
        if (attributes != null) {
            for (Attribute attribute : attributes) {
                i.addAttribute(attribute);
            }
        }

        session.save(i);
        session.evict(i);
        categoryMap.put(i.getId(), i);
        return i;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addAttribute(Category category, Attribute attribute) {
        final Session session = sessionFactory.getCurrentSession();

        final HibernateCategory hibernateCategory = categoryMap.get(category.getId());
        hibernateCategory.addAttribute(attribute);
        session.merge(hibernateCategory);
        session.evict(hibernateCategory);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void removeAttribute(Category category, Attribute attribute) {
        final Session session = sessionFactory.getCurrentSession();

        final HibernateCategory hibernateCategory = categoryMap.get(category.getId());
        hibernateCategory.removeAttribute(attribute);
        session.merge(hibernateCategory);
        session.evict(hibernateCategory);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setAttributeManager(AttributeManager attributeManager) {
        this.attributeManager = attributeManager;
    }
}
