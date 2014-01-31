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
public abstract class EntitySearchManager<E, C, F> implements SearchManager<E, C, F> {
	private final Class<?> entityType;

	protected SessionFactory sessionFactory;

	protected EntitySearchManager(Class<?> entityType) {
		this.entityType = entityType;
	}

	@Override
	public <Ctx extends C> int getTotalCount(Ctx context) {
		return getTotalCount(context, null);
	}

	@Override
	public <Ctx extends C, Ftl extends F> int getTotalCount(Ctx context, Ftl filter) {
		final Session session = sessionFactory.getCurrentSession();

		final Criteria criteria = session.createCriteria(entityType);
		applyRestrictions(criteria, context, filter);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).intValue();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <Ctx extends C, Ftl extends F> List<E> searchEntities(Ctx context, Ftl filter, Range range, Orders orders) {
		final Session session = sessionFactory.getCurrentSession();

		final Criteria criteria = session.createCriteria(entityType);
		applyRestrictions(criteria, context, filter);
		applyProjections(criteria, context, filter);
		applyOrders(criteria, context, orders);
		applyRange(range, context, criteria);

		final List list = criteria.list();
		initializeEntities(list);
		return list;
	}

	protected abstract void applyRestrictions(Criteria criteria, C context, F filter);

	protected abstract void applyProjections(Criteria criteria, C context, F filter);

	protected void initializeEntities(List<E> list) {
	}

	protected void applyRange(Range range, C context, Criteria criteria) {
		if (range != null) {
			range.apply(criteria);
		}
	}

	protected void applyOrders(Criteria criteria, C context, Orders orders) {
		if (orders != null) {
			orders.apply(criteria);
		}
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}