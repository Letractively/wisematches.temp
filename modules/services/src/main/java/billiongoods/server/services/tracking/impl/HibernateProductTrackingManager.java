package billiongoods.server.services.tracking.impl;

import billiongoods.core.account.Account;
import billiongoods.core.search.entity.EntitySearchManager;
import billiongoods.server.services.tracking.*;
import billiongoods.server.warehouse.*;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateProductTrackingManager extends EntitySearchManager<ProductTracking, TrackingContext, Void> implements ProductTrackingManager {
	private ProductManager productManager;

	private final ProductStateListener productStateListener = new TheStateProductListener();
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
	public ProductTracking createTracking(Integer productId, TrackingPerson tracker, TrackingType type) {
		final Session session = sessionFactory.getCurrentSession();

		final ProductTracking productTracking = getTracking(productId, tracker, type);
		if (productTracking != null) {
			return productTracking;
		}

		final HibernateProductTracking tracking = new HibernateProductTracking(productId, tracker, type);
		session.save(tracking);

		for (ProductTrackingListener listener : listeners) {
			listener.trackingAdded(tracking);
		}
		return tracking;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public ProductTracking removeTracking(Integer productId, TrackingPerson tracker, TrackingType type) {
		final Session session = sessionFactory.getCurrentSession();

		final ProductTracking tracking = getTracking(productId, tracker, type);
		if (tracking != null) {
			session.delete(tracking);

			for (ProductTrackingListener listener : listeners) {
				listener.trackingRemoved(tracking);
			}
		}
		return tracking;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public ProductTracking getTracking(Integer productId, TrackingPerson tracker, TrackingType type) {
		final Session session = sessionFactory.getCurrentSession();

		Query query;
		if (tracker instanceof TrackingPerson.Member) {
			final TrackingPerson.Member member = (TrackingPerson.Member) tracker;
			query = session.createQuery("from billiongoods.server.services.tracking.impl.HibernateProductTracking t " +
					"where t.productId = :productId and t.trackingType=:trackingType and t.personId=:personId");
			query.setLong("personId", member.getPersonId());
		} else if (tracker instanceof TrackingPerson.Visitor) {
			final TrackingPerson.Visitor visitor = (TrackingPerson.Visitor) tracker;

			query = session.createQuery("from billiongoods.server.services.tracking.impl.HibernateProductTracking t " +
					"where t.productId = :productId and t.trackingType=:trackingType and t.personEmail=:personEmail");
			query.setString("personEmail", visitor.getPersonEmail());
		} else {
			throw new IllegalArgumentException("Incorrect tracker type: " + tracker);
		}

		query.setInteger("productId", productId);
		query.setParameter("trackingType", type);
		return (ProductTracking) query.uniqueResult();
	}

	@Override
	public EnumSet<TrackingType> containsTracking(Integer productId, TrackingPerson tracker) {
		final Session session = sessionFactory.getCurrentSession();

		Query query;
		if (tracker instanceof TrackingPerson.Member) {
			final TrackingPerson.Member member = (TrackingPerson.Member) tracker;
			query = session.createQuery("select t.trackingType from billiongoods.server.services.tracking.impl.HibernateProductTracking t " +
					"where t.productId = :productId and t.personId=:personId");
			query.setLong("personId", member.getPersonId());
		} else if (tracker instanceof TrackingPerson.Visitor) {
			final TrackingPerson.Visitor visitor = (TrackingPerson.Visitor) tracker;

			query = session.createQuery("select t.trackingType from billiongoods.server.services.tracking.impl.HibernateProductTracking t " +
					"where t.productId = :productId and t.personEmail=:personEmail");
			query.setString("personEmail", visitor.getPersonEmail());
		} else {
			throw new IllegalArgumentException("Incorrect tracker type: " + tracker);
		}

		query.setInteger("productId", productId);

		@SuppressWarnings("unchecked")
		final List<TrackingType> list = query.list();
		if (list.isEmpty()) {
			return EnumSet.noneOf(TrackingType.class);
		}
		return EnumSet.copyOf(list);
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

			final TrackingPerson trackingPerson = context.getTrackingPerson();
			if (trackingPerson instanceof TrackingPerson.Member) {
				final TrackingPerson.Member person = (TrackingPerson.Member) trackingPerson;
				criteria.add(Restrictions.eq("personId", person.getPersonId()));
			} else if (trackingPerson instanceof TrackingPerson.Visitor) {
				final TrackingPerson.Visitor person = (TrackingPerson.Visitor) trackingPerson;
				criteria.add(Restrictions.eq("personEmail", person.getPersonEmail()));
			}

			if (context.getTrackingType() != null) {
				criteria.add(Restrictions.eq("trackingType", context.getTrackingType()));
			}
		}
	}

	private void processTrackingNotifications(ProductPreview preview, TrackingType type) {
		final Session session = sessionFactory.getCurrentSession();

		final Query query = session.createQuery("from billiongoods.server.services.tracking.impl.HibernateProductTracking t where t.productId=:pid and t.trackingType=:type");
		query.setInteger("pid", preview.getId());
		query.setParameter("type", type);

		for (Object o : query.list()) {
			final HibernateProductTracking tracking = (HibernateProductTracking) o;
			for (ProductTrackingListener listener : listeners) {
				listener.trackingInvalidated(tracking);
			}

			session.delete(tracking);
			for (ProductTrackingListener listener : listeners) {
				listener.trackingRemoved(tracking);
			}
		}
	}

	public void setProductManager(ProductManager productManager) {
		if (this.productManager != null) {
			this.productManager.removeProductStateListener(productStateListener);
		}

		this.productManager = productManager;

		if (this.productManager != null) {
			this.productManager.addProductStateListener(productStateListener);
		}
	}

	private class TheStateProductListener implements ProductStateListener {
		private TheStateProductListener() {
		}

		@Override
		public void productPriceChanged(ProductPreview preview, Price oldPrice, Price newPrice) {
		}

		@Override
		public void productStockChanged(ProductPreview preview, StockInfo oldStock, StockInfo newStock) {
			if (newStock.getStockState() == StockState.IN_STOCK) {
				processTrackingNotifications(preview, TrackingType.AVAILABILITY);
			}
		}

		@Override
		public void productStateChanged(ProductPreview preview, ProductState oldState, ProductState newState) {
			if (newState == ProductState.ACTIVE && oldState != ProductState.ACTIVE) {
				processTrackingNotifications(preview, TrackingType.DESCRIPTION);
			}
		}
	}
}
