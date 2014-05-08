package billiongoods.server.services.showcase.impl;

import billiongoods.server.services.showcase.Showcase;
import billiongoods.server.services.showcase.ShowcaseListener;
import billiongoods.server.services.showcase.ShowcaseManager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateShowcaseManager implements ShowcaseManager, InitializingBean {
	private SessionFactory sessionFactory;

	private Showcase showcase = new DefaultShowcase();
	private final Collection<ShowcaseListener> listeners = new ArrayList<>();

	public HibernateShowcaseManager() {
	}

	@Override
	public void addShowcaseListener(ShowcaseListener l) {
		if (l != null) {
			listeners.add(l);
		}
	}

	@Override
	public void removeShowcaseListener(ShowcaseListener l) {
		if (l != null) {
			listeners.remove(l);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		reloadShowcase();
	}

	@Override
	public void createItem(Integer section, Integer position, String name, Integer category, boolean arrival, boolean sub) {
		final Session session = sessionFactory.getCurrentSession();

		HibernateShowcaseItem item = (HibernateShowcaseItem) session.get(HibernateShowcaseItem.class, new HibernateShowcaseItem.Pk(section, position));
		if (item == null) {
			item = new HibernateShowcaseItem(section, position, name, category, arrival, sub);
			session.save(item);
		} else {
			item.setName(name);
			item.setCategory(category);
			item.setArrival(arrival);
			item.setSubCategories(sub);
			session.update(item);
		}
		reloadShowcaseImpl(session);
	}

	@Override
	public void removeItem(Integer section, Integer position) {
		final Session session = sessionFactory.getCurrentSession();

		HibernateShowcaseItem item = (HibernateShowcaseItem) session.get(HibernateShowcaseItem.class, new HibernateShowcaseItem.Pk(section, position));
		if (item != null) {
			session.delete(item);
			reloadShowcaseImpl(session);
		}
	}

	@Override
	public Showcase getShowcase() {
		return showcase;
	}

	@Override
	public void reloadShowcase() {
		final Session session = sessionFactory.openSession();
		try {
			reloadShowcaseImpl(session);
		} finally {
			session.flush();
			session.close();
		}
	}

	private void reloadShowcaseImpl(Session session) {
		final Query query = session.createQuery("from billiongoods.server.services.showcase.impl.HibernateShowcaseItem order by pk.section, pk.position asc");

		final List<HibernateShowcaseItem> items = new ArrayList<>();
		for (Object o : query.list()) {
			items.add((HibernateShowcaseItem) o);
			session.evict(o);
		}
		showcase = new DefaultShowcase(items);

		for (ShowcaseListener listener : listeners) {
			listener.showcaseInvalidated(showcase);
		}
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}

