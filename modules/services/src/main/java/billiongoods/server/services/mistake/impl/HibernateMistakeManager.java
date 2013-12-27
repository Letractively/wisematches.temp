package billiongoods.server.services.mistake.impl;

import billiongoods.server.services.mistake.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateMistakeManager implements MistakeManager {
	private SessionFactory sessionFactory;

	private final Collection<MistakeListener> listeners = new CopyOnWriteArraySet<>();

	public HibernateMistakeManager() {
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void addMistakeListener(MistakeListener l) {
		if (l != null) {
			listeners.add(l);
		}
	}

	@Override
	public void removeMistakeListener(MistakeListener l) {
		if (l != null) {
			listeners.remove(l);
		}
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Mistake raiseMistake(Integer productId, String description, MistakeScope scope) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateMistake mistake = new HibernateMistake(productId, description, scope);
		session.save(mistake);

		for (MistakeListener listener : listeners) {
			listener.mistakeRaised(mistake);
		}

		return mistake;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Mistake resolveMistake(Integer mistakeId, MistakeResolution resolution) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateMistake mistake = (HibernateMistake) session.get(HibernateMistake.class, mistakeId);
		if (mistake != null) {
			mistake.resolve();
			session.update(mistake);
		}

		return mistake;
	}
}
