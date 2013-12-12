package billiongoods.server.services.tracking.impl;

import billiongoods.core.Personality;
import billiongoods.core.account.Account;
import billiongoods.core.search.entity.EntitySearchManager;
import billiongoods.server.services.tracking.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
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
	public ProductTracking getTracking(Integer productId, Personality person) {
		final Session session = sessionFactory.getCurrentSession();
		final Query query = session.createQuery("from billiongoods.server.services.tracking.impl.HibernateProductTracking where productId=:prid and personId=:pid");
		query.setInteger("prid", productId);
		query.setLong("pid", person.getId());

		final List list = query.list();
		if (list.isEmpty()) {
			return null;
		}
		return (ProductTracking) list.get(0);
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
	@Transactional(propagation = Propagation.MANDATORY)
	public int importAccountTracking(Account account) {
		if (account.getEmail() != null) {
			final Session session = sessionFactory.getCurrentSession();

			final Query query = session.createQuery("update billiongoods.server.services.tracking.impl.HibernateProductTracking t set t.personId = :pid where t.personEmail = :email");
			query.setLong("pid", account.getId());
			query.setString("email", account.getEmail());
			return query.executeUpdate();
		}
		return 0;
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

			if (context.getPersonality() != null) {
				criteria.add(Restrictions.eq("personId", context.getPersonality().getId()));
			}

			if (context.getTrackingType() != null) {
				criteria.add(Restrictions.eq("trackingType", context.getTrackingType()));
			}
		}
	}
}
