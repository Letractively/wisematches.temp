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
	private ArticleManager articleManager;

	public HibernateRelationshipManager() {
	}

	@Override
	public HibernateGroup getGroup(Integer id) {
		return (HibernateGroup) sessionFactory.getCurrentSession().get(HibernateGroup.class, id);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Group createGroup(String name) {
		final HibernateGroup group = new HibernateGroup(name);
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
	public Group updateGroup(Integer id, String name) {
		final Session session = sessionFactory.getCurrentSession();
		final HibernateGroup group = getGroup(id);
		if (group != null) {
			group.setName(name);
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
	public List<Group> getGroups(Integer articleId) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("select g from billiongoods.server.warehouse.impl.HibernateGroup g join g.articles a where a.id=:article");
		query.setParameter("article", articleId);
		return query.list();
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Group addGroupItem(Integer groupId, Integer article) {
		final Session session = sessionFactory.getCurrentSession();
		final ArticleDescription description = articleManager.getDescription(article);
		final HibernateGroup group = getGroup(groupId);
		if (group.addArticle(description)) {
			session.update(group);
		}
		return group;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Group removeGroupItem(Integer groupId, Integer article) {
		final Session session = sessionFactory.getCurrentSession();
		final ArticleDescription description = articleManager.getDescription(article);
		final HibernateGroup group = getGroup(groupId);
		if (group.removeArticle(description)) {
			session.update(group);
		}
		return group;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void addRelationship(Integer articleId, Integer groupId, RelationshipType type) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateGroup group = getGroup(groupId);
		if (group == null) {
			throw new IllegalArgumentException("Unknown group: " + groupId);
		}
		session.save(new HibernateRelationship(group, type, articleId));
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void removeRelationship(Integer articleId, Integer groupId, RelationshipType type) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateGroup group = getGroup(groupId);
		if (group == null) {
			throw new IllegalArgumentException("Unknown group: " + groupId);
		}

		final HibernateRelationship relationship = (HibernateRelationship) session.get(HibernateRelationship.class, new HibernateRelationship.Pk(articleId, type, group));
		if (relationship != null) {
			session.delete(relationship);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Relationship> getRelationships(Integer articleId) {
		final Session session = sessionFactory.getCurrentSession();

		final Query query = session.createQuery("from billiongoods.server.warehouse.impl.HibernateRelationship where pk.articleId=:aid");
		query.setParameter("aid", articleId);
		return query.list();
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setArticleManager(ArticleManager articleManager) {
		this.articleManager = articleManager;
	}
}
