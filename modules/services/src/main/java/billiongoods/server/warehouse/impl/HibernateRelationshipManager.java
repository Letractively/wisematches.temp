package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateRelationshipManager implements RelationshipManager {
	private SessionFactory sessionFactory;
	private ProductManager productManager;

	public HibernateRelationshipManager() {
	}

	@Override
	public HibernateGroup getGroup(Integer id) {
		return (HibernateGroup) sessionFactory.getCurrentSession().get(HibernateGroup.class, id);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Group createGroup(String name, GroupType type, Integer categoryId) {
		final HibernateGroup group = new HibernateGroup(name, type, categoryId);
		sessionFactory.getCurrentSession().save(group);
		return group;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Group removeGroup(Integer id) {
		final Session session = sessionFactory.getCurrentSession();
		final HibernateGroup group = getGroup(id);
		if (group != null) {
			session.delete(group);
		}
		return group;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Group updateGroup(Integer id, String name, GroupType type, Integer categoryId) {
		final Session session = sessionFactory.getCurrentSession();
		final HibernateGroup group = getGroup(id);
		if (group != null) {
			group.setName(name);
			group.setType(type);
			group.setCategoryId(categoryId);
			session.update(group);
		}
		return group;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<Group> searchGroups(String name) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("from billiongoods.server.warehouse.impl.HibernateGroup g where g.name like:name");
		query.setParameter("name", "%" + name + "%");
		return query.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<Group> searchGroups(Integer categoryId) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("from billiongoods.server.warehouse.impl.HibernateGroup g where g.categoryId=:cid");
		query.setParameter("cid", categoryId);
		return query.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<Group> getGroups(Integer productId) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("select g from billiongoods.server.warehouse.impl.HibernateGroup g join g.products a where a.id=:product");
		query.setParameter("product", productId);
		return query.list();
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Group addGroupItem(Integer groupId, Integer productId) {
		final Session session = sessionFactory.getCurrentSession();
		final ProductPreview preview = productManager.getPreview(productId);
		final HibernateGroup group = getGroup(groupId);
		if (group == null) {
			return null;
		}
		if (group.addProduct(preview)) {
			session.update(group);
		}
		return group;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Group removeGroupItem(Integer groupId, Integer productId) {
		final Session session = sessionFactory.getCurrentSession();
		final ProductPreview preview = productManager.getPreview(productId);
		final HibernateGroup group = getGroup(groupId);
		if (group == null) {
			return null;
		}
		if (group.removeProduct(preview)) {
			session.update(group);
		}
		return group;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Relationship addRelationship(Integer productId, Integer groupId, RelationshipType type) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateGroup group = getGroup(groupId);
		if (group == null) {
			throw new IllegalArgumentException("Unknown group: " + groupId);
		}
		final HibernateRelationship object = new HibernateRelationship(productId, group, type);
		session.save(object);
		return object;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Relationship removeRelationship(Integer productId, Integer groupId, RelationshipType type) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateGroup group = getGroup(groupId);
		if (group == null) {
			throw new IllegalArgumentException("Unknown group: " + groupId);
		}

		final HibernateRelationship relationship = (HibernateRelationship) session.get(HibernateRelationship.class, new HibernateRelationship.Pk(productId, type, group));
		if (relationship != null) {
			session.delete(relationship);
		}
		return relationship;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Relationship> getRelationships(Integer productId) {
		final Session session = sessionFactory.getCurrentSession();

		final Query query = session.createQuery("from billiongoods.server.warehouse.impl.HibernateRelationship where pk.productId=:productId");
		query.setParameter("productId", productId);
		return query.list();
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}
}
