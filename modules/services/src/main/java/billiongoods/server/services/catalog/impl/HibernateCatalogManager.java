package billiongoods.server.services.catalog.impl;

import billiongoods.server.services.catalog.CatalogItem;
import billiongoods.server.services.catalog.CatalogManager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateCatalogManager implements CatalogManager, InitializingBean {
    private SessionFactory sessionFactory;

    private final HibernateCatalogItem rootCatalogItem = new HibernateCatalogItem();

    private static final Logger log = LoggerFactory.getLogger("billiongoods.store.HibernateCatalogManager");

    public HibernateCatalogManager() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() throws Exception {
        final Session session = sessionFactory.openSession();
        final Query query = session.createQuery("select i from billiongoods.server.services.catalog.impl.HibernateCatalogItem i where i.parent is null");
        for (Object o : query.list()) {
            rootCatalogItem.addChild((HibernateCatalogItem) o);
        }
    }

    @Override
    public CatalogItem getCatalog() {
        return rootCatalogItem;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CatalogItem addCatalogItem(String name, CatalogItem parent) {
        final Session session = sessionFactory.getCurrentSession();

        HibernateCatalogItem p = (HibernateCatalogItem) parent;
        if (p == null) {
            p = rootCatalogItem;
        }

        final HibernateCatalogItem i = new HibernateCatalogItem(name, p);
        session.save(i);
        return i;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public CatalogItem removeCatalogItem(CatalogItem item, CatalogItem newParent) {
        final Session session = sessionFactory.getCurrentSession();

        final HibernateCatalogItem hItem = (HibernateCatalogItem) item;
        final HibernateCatalogItem hNewParent = (HibernateCatalogItem) newParent;

        session.delete(hItem);
        for (CatalogItem ci : new ArrayList<>(hItem.getCatalogItems())) {
            final HibernateCatalogItem hci = (HibernateCatalogItem) ci;
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
