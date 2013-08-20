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
	public Category createCategory(String name, String description, Set<Attribute> attributes, Category parent, int position) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateCategory p = (HibernateCategory) parent;
		final HibernateCategory i = new HibernateCategory(name, description, p, position, attributes);

		session.save(i);
		categoryMap.put(i.getId(), i);

		if (parent == null) {
			catalog.addRootCategory(i);
		}

		return i;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Category updateCategory(Integer id, String name, String description, Set<Attribute> attributes, Category parent, int position) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateCategory hc = (HibernateCategory) session.get(HibernateCategory.class, id);
		if (hc == null) {
			throw new IllegalArgumentException("There is no category: " + id);
		}

		final Category parent1 = hc.getParent();
		if (parent1 == null) {
			catalog.removeRootCategory(hc);
		}

		hc.setName(name);
		hc.setDescription(description);
		hc.setParent((HibernateCategory) parent);
		hc.setPosition(position);
		hc.setAttributes(attributes);

		session.update(hc);
		categoryMap.put(hc.getId(), hc);

		if (hc.getParent() == null) {
			catalog.addRootCategory(hc);
		}

		return hc;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setAttributeManager(AttributeManager attributeManager) {
		this.attributeManager = attributeManager;
	}
}
