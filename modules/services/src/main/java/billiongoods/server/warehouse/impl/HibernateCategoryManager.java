package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.Catalog;
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

import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateCategoryManager implements CategoryManager, InitializingBean {
	private SessionFactory sessionFactory;

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
		}

		for (HibernateCategory category : categoryMap.values()) {
			category.preInit(categoryMap.get(category.getParentId()));
		}

		final List<Category> rootCategories = new ArrayList<>();
		for (HibernateCategory category : categoryMap.values()) {
			category.postInit();

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
	public Category addCatalogItem(String name, Category parent) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateCategory p = (HibernateCategory) parent;
		final HibernateCategory i = new HibernateCategory(name, p);
		session.save(i);
		return i;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Category removeCatalogItem(Category item, Category newParent) {
/*
		final Session session = sessionFactory.getCurrentSession();

		final HibernateCategory hItem = (HibernateCategory) item;
		final HibernateCategory hNewParent = (HibernateCategory) newParent;

		session.delete(hItem);
		for (Category ci : new ArrayList<>(hItem.getChildren())) {
			final HibernateCategory hci = (HibernateCategory) ci;
			hci.removeFromParent();
			hNewParent.addChild(hci);
		}
//        session.update(hNewParent);
		hItem.removeFromParent();
*/
		return item;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
