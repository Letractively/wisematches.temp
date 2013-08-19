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
	@Transactional(propagation = Propagation.MANDATORY)
	public Group createGroup(String name) {
		final HibernateGroup group = new HibernateGroup(name);
		sessionFactory.getCurrentSession().save(group);
		return group;
	}

	@Override
	public Group deleteGroup(Integer id) {
		final Session session = sessionFactory.getCurrentSession();
		final HibernateGroup group = (HibernateGroup) session.get(HibernateGroup.class, id);
		if (group != null) {
			session.delete(group);
		}
		return group;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<Group> getGroups(Integer articleId) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("from billiongoods.server.warehouse.impl.HibernateGroup g join g.articles a where a.id=:article");
		query.setParameter("article", articleId);
		return query.list();
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Group addGroupItem(Integer groupId, Integer article) {
		final Session session = sessionFactory.getCurrentSession();
		final ArticleDescription description = articleManager.getDescription(article);
		final HibernateGroup group = (HibernateGroup) session.get(HibernateGroup.class, groupId);
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
		final HibernateGroup group = (HibernateGroup) session.get(HibernateGroup.class, groupId);
		if (group.removeArticle(description)) {
			session.update(group);
		}
		return group;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void changeRelationship(Integer articleId, RelationshipType type, Integer groupId) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateGroup group = (HibernateGroup) session.get(HibernateGroup.class, groupId);
		HibernateRelationship relationship = (HibernateRelationship) session.get(HibernateRelationship.class, new HibernateRelationship.Pk(articleId, type));
		if (groupId == null) {
			if (relationship != null) {
				session.delete(relationship);
			}
		} else {
			if (relationship == null) {
				relationship = new HibernateRelationship(articleId, type, group);
				session.save(relationship);
			} else {
				relationship.setGroup(group);
				session.update(relationship);
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Relationships getRelationships(ArticleDescription description) {
		final Session session = sessionFactory.getCurrentSession();

		final Query query = session.createQuery("from billiongoods.server.warehouse.impl.HibernateRelationship where pk.articleId=:aid");
		query.setParameter("aid", description.getId());
		return new DefaultRelationships(query.list());
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setArticleManager(ArticleManager articleManager) {
		this.articleManager = articleManager;
	}
}
