package billiongoods.server.services.state.impl;

import billiongoods.core.Member;
import billiongoods.core.Personality;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernatePersonalityStateManager extends SessionRegistryStateManager {
	private SessionFactory sessionFactory;

	private final Map<Personality, Date> lastActivityMap = new HashMap<>();

	public HibernatePersonalityStateManager() {
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void onApplicationEvent(SessionDestroyedEvent event) {
		super.onApplicationEvent(event);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Date getLastActivityDate(Personality personality) {
		final Date lastActivityDate = lastActivityMap.get(personality);
		if (lastActivityDate == null && personality instanceof Member) {
			final Session session = sessionFactory.getCurrentSession();
			HibernatePersonalityActivity a = (HibernatePersonalityActivity) session.get(HibernatePersonalityActivity.class, personality.getId());
			if (a != null) { // DON'T PUT TO lastActivityMap. PLAYER IS OFFLINE AND CACHE WON'T BE CLEANED
				return a.getLastActivityDate();
			}
		}
		return lastActivityDate;
	}

	@Override
	protected void processPersonalityOnline(Personality player) {
		super.processPersonalityOnline(player);

		lastActivityMap.put(player, new Date());
	}

	@Override
	protected void processPersonalityAlive(Personality player) {
		super.processPersonalityAlive(player);

		lastActivityMap.put(player, new Date());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	protected void processPersonalityOffline(Personality personality) {
		super.processPersonalityOffline(personality);

		final Date remove = lastActivityMap.remove(personality);
		if (remove != null && personality instanceof Member) {
			final Session session = sessionFactory.getCurrentSession();
			HibernatePersonalityActivity a = (HibernatePersonalityActivity) session.get(HibernatePersonalityActivity.class, personality.getId());
			if (a == null) {
				a = new HibernatePersonalityActivity(personality.getId(), remove);
				session.save(a);
			} else {
				a.setLastActivityDate(remove);
				session.update(a);
			}
		}
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
