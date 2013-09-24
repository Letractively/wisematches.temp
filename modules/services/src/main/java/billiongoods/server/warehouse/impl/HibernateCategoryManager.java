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

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateCategoryManager implements CategoryManager, InitializingBean {
	private SessionFactory sessionFactory;
	private AttributeManager attributeManager;

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
	public Category createCategory(Category.Editor editor) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateCategory i = new HibernateCategory(editor.getName(), editor.getDescription(),
				editor.getParent() != null ? editor.getParent().getId() : null, editor.getPosition());
		session.save(i);
		return catalog.addCategory(i, attributeManager);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Category updateCategory(Category.Editor editor) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateCategory hc = (HibernateCategory) session.get(HibernateCategory.class, editor.getId());
		if (hc == null) {
			throw new IllegalArgumentException("There is no category: " + editor.getId());
		}

		hc.setName(editor.getName());
		hc.setDescription(editor.getDescription());
		hc.setParentId(editor.getParent() != null ? editor.getParent().getId() : null);
		hc.setPosition(editor.getPosition());

		session.update(hc);
		return catalog.updateCategory(hc, attributeManager);
	}

	@Override
	public void addParameter(Category category, Attribute attribute) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateCategory hc = (HibernateCategory) session.get(HibernateCategory.class, category.getId());
		if (hc == null) {
			throw new IllegalArgumentException("There is no category: " + category.getId());
		}

		if (hc.addParameter(attribute)) {
			session.update(hc);
			catalog.updateCategory(hc, attributeManager);
		}
	}

	@Override
	public void removeParameter(Category category, Attribute attribute) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateCategory hc = (HibernateCategory) session.get(HibernateCategory.class, category.getId());
		if (hc == null) {
			throw new IllegalArgumentException("There is no category: " + category.getId());
		}

		if (hc.removeParameter(attribute)) {
			session.update(hc);
			catalog.updateCategory(hc, attributeManager);
		}
	}

	@Override
	public void addParameterValue(Category category, Attribute attribute, String value) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateCategory hc = (HibernateCategory) session.get(HibernateCategory.class, category.getId());
		if (hc == null) {
			throw new IllegalArgumentException("There is no category: " + category.getId());
		}

		if (hc.addParameterValue(attribute, value)) {
			session.update(hc);
			catalog.updateCategory(hc, attributeManager);
		}
	}

	@Override
	public void removeParameterValue(Category category, Attribute attribute, String value) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateCategory hc = (HibernateCategory) session.get(HibernateCategory.class, category.getId());
		if (hc == null) {
			throw new IllegalArgumentException("There is no category: " + category.getId());
		}

		if (hc.removeParameterValue(attribute, value)) {
			session.update(hc);
			catalog.updateCategory(hc, attributeManager);
		}
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setAttributeManager(AttributeManager attributeManager) {
		this.attributeManager = attributeManager;
	}
}
