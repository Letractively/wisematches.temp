package billiongoods.server.services.state.impl;

import billiongoods.core.Personality;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernatePlayerStateManager extends SessionRegistryStateManager {
	private SessionFactory sessionFactory;
	private final Map<Personality, Date> lastActivityMap = new HashMap<>();

	public HibernatePlayerStateManager() {
	}

	@Override
	protected void processPlayerOnline(Personality player) {
		super.processPlayerOnline(player);
		lastActivityMap.put(player, new Date());
	}

	@Override
	protected void processPlayerAlive(Personality player) {
		super.processPlayerAlive(player);
		lastActivityMap.put(player, new Date());
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Date getLastActivityDate(Personality personality) {
		final Date lastActivityDate = lastActivityMap.get(personality);
		if (lastActivityDate == null) {
			final Session session = sessionFactory.getCurrentSession();
			HibernatePlayerActivity a = (HibernatePlayerActivity) session.get(HibernatePlayerActivity.class, personality.getId());
			if (a != null) { // DON'T PUT TO lastActivityMap. PLAYER IS OFFLINE AND CACHE WON'T BE CLEANED
				return a.getLastActivityDate();
			}
		}
		return lastActivityDate;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	protected void processPlayerOffline(Personality player) {
		super.processPlayerOffline(player);
		final Date remove = lastActivityMap.remove(player);
		if (remove != null) {
			final Session session = sessionFactory.getCurrentSession();
			HibernatePlayerActivity a = (HibernatePlayerActivity) session.get(HibernatePlayerActivity.class, player.getId());
			if (a == null) {
				a = new HibernatePlayerActivity(player.getId(), remove);
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
