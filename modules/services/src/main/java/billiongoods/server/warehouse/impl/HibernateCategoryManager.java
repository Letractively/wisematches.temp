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

import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateCategoryManager implements CategoryManager, InitializingBean {
	private SessionFactory sessionFactory;
	private StoreAttributeManager attributeManager;

	private final DefaultCatalog catalog = new DefaultCatalog();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.warehouse.HibernateCategoryManager");

	public HibernateCategoryManager() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		final Session session = sessionFactory.openSession();

		final Query query = session.createQuery("from billiongoods.server.warehouse.impl.HibernateCategory");
		catalog.initialize(query.list(), attributeManager);
	}

	@Override
	public Catalog getCatalog() {
		return catalog;
	}

	@Override
	public Category getCategory(Integer id) {
		return catalog.getCategory(id);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Category createCategory(String name, String description, Set<StoreAttribute> attributes, Category parent, int position) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateCategory i = new HibernateCategory(name, description, parent != null ? parent.getId() : null, position, attributes);
		session.save(i);
		return catalog.addCategory(i, attributeManager);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Category updateCategory(Integer id, String name, String description, Set<StoreAttribute> attributes, Category parent, int position) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateCategory hc = (HibernateCategory) session.get(HibernateCategory.class, id);
		if (hc == null) {
			throw new IllegalArgumentException("There is no category: " + id);
		}

		hc.setName(name);
		hc.setDescription(description);
		hc.setParentId(parent != null ? parent.getId() : null);
		hc.setPosition(position);
		hc.setAttributes(attributes);

		session.update(hc);
		return catalog.updateCategory(hc, attributeManager);
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setAttributeManager(StoreAttributeManager attributeManager) {
		this.attributeManager = attributeManager;
	}
}
