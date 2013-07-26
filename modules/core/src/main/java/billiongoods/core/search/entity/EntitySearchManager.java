package billiongoods.core.search.entity;

import billiongoods.core.search.Orders;
import billiongoods.core.search.Range;
import billiongoods.core.search.SearchManager;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;

import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class EntitySearchManager<E, C> implements SearchManager<E, C> {
	private final Class<?> entityType;

	protected SessionFactory sessionFactory;

	protected EntitySearchManager(Class<?> entityType) {
		this.entityType = entityType;
	}

	@Override
	public <Ctx extends C> int getTotalCount(Ctx context) {
		final Session session = sessionFactory.getCurrentSession();

		final Criteria criteria = session.createCriteria(entityType);
		applyRestrictions(criteria, context);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <Ctx extends C> List<E> searchEntities(Ctx context, Orders orders, Range range) {
		final Session session = sessionFactory.getCurrentSession();

		final Criteria criteria = session.createCriteria(entityType);
		applyRestrictions(criteria, context);
		applyProjections(criteria, context);
		applyOrders(criteria, orders);
		applyRange(range, criteria);

		final List list = criteria.list();
		initializeEntities(list);
		return list;
	}

	protected abstract void applyRestrictions(Criteria criteria, C context);

	protected abstract void applyProjections(Criteria criteria, C context);

	protected void initializeEntities(List<E> list) {
	}

	protected void applyRange(Range range, Criteria criteria) {
		if (range != null) {
			range.apply(criteria);
		}
	}

	protected void applyOrders(Criteria criteria, Orders orders) {
		if (orders != null) {
			orders.apply(criteria);
		}
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}