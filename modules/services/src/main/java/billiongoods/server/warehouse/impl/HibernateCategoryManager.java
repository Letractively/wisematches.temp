package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.CategoryManager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateCategoryManager implements CategoryManager, InitializingBean {
    private SessionFactory sessionFactory;

    private final HibernateCategory rootCatalogItem = new HibernateCategory();

    private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.HibernateCategoryManager");

    public HibernateCategoryManager() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception {
        final Session session = sessionFactory.openSession();
        final Query query = session.createQuery("select i from billiongoods.server.warehouse.impl.HibernateCategory i where i.parent is null");
        final List list = query.list();
        for (Object o : list) {
            rootCatalogItem.addChild((HibernateCategory) o);
        }
    }

    @Override
    public Category getCatalog() {
        return rootCatalogItem;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Category addCatalogItem(String name, Category parent) {
        final Session session = sessionFactory.getCurrentSession();

        HibernateCategory p = (HibernateCategory) parent;
        if (p == null) {
            p = rootCatalogItem;
        }

        final HibernateCategory i = new HibernateCategory(name, p);
        session.save(i);
        return i;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Category removeCatalogItem(Category item, Category newParent) {
        final Session session = sessionFactory.getCurrentSession();

        final HibernateCategory hItem = (HibernateCategory) item;
        final HibernateCategory hNewParent = (HibernateCategory) newParent;

        session.delete(hItem);
        for (Category ci : new ArrayList<>(hItem.getCatalogItems())) {
            final HibernateCategory hci = (HibernateCategory) ci;
            hci.removeFromParent();
            hNewParent.addChild(hci);
        }
//        session.update(hNewParent);
        hItem.removeFromParent();
        return item;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
