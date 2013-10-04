package billiongoods.server.services.tracking.impl;

import billiongoods.core.Personality;
import billiongoods.core.search.entity.EntitySearchManager;
import billiongoods.server.services.tracking.*;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateProductTrackingManager extends EntitySearchManager<ProductTracking, TrackingContext, Void> implements ProductTrackingManager {
	private final Collection<ProductTrackingListener> listeners = new CopyOnWriteArrayList<>();

	public HibernateProductTrackingManager() {
		super(HibernateProductTracking.class);
	}

	@Override
	public void addProductTrackingListener(ProductTrackingListener l) {
		if (l != null) {
			listeners.add(l);
		}
	}

	@Override
	public void removeProductTrackingListener(ProductTrackingListener l) {
		if (l != null) {
			listeners.remove(l);
		}
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public ProductTracking createTracking(Integer productId, String email, TrackingType trackingType) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateProductTracking tracking = new HibernateProductTracking(productId, email, trackingType);
		session.save(tracking);

		for (ProductTrackingListener listener : listeners) {
			listener.trackingAdded(tracking);
		}
		return tracking;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public ProductTracking createTracking(Integer productId, Personality person, TrackingType trackingType) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateProductTracking tracking = new HibernateProductTracking(productId, person.getId(), trackingType);
		session.save(tracking);

		for (ProductTrackingListener listener : listeners) {
			listener.trackingAdded(tracking);
		}
		return tracking;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public ProductTracking getTracking(Integer id) {
		final Session session = sessionFactory.getCurrentSession();
		return (HibernateProductTracking) session.get(HibernateProductTracking.class, id);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public ProductTracking removeTracking(Integer id) {
		final Session session = sessionFactory.getCurrentSession();
		final HibernateProductTracking pt = (HibernateProductTracking) session.get(HibernateProductTracking.class, id);
		if (pt != null) {
			session.delete(pt);
			for (ProductTrackingListener listener : listeners) {
				listener.trackingRemoved(pt);
			}
		}
		return pt;
	}

	@Override
	protected void applyProjections(Criteria criteria, TrackingContext context, Void filter) {
	}

	@Override
	protected void applyRestrictions(Criteria criteria, TrackingContext context, Void filter) {
		if (context != null) {
			if (context.getProductId() != null) {
				criteria.add(Restrictions.eq("productId", context.getProductId()));
			}

			if (context.getEmail() != null) {
				criteria.add(Restrictions.eq("personEmail", context.getEmail()));
			}

			if (context.getPersonId() != null) {
				criteria.add(Restrictions.eq("personId", context.getPersonId()));
			}

			if (context.getTrackingType() != null) {
				criteria.add(Restrictions.eq("trackingType", context.getTrackingType()));
			}
		}
	}
}
