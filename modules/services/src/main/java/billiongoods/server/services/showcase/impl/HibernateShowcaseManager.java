package billiongoods.server.services.showcase.impl;

import billiongoods.server.services.showcase.Showcase;
import billiongoods.server.services.showcase.ShowcaseListener;
import billiongoods.server.services.showcase.ShowcaseManager;
import billiongoods.server.warehouse.CategoryManager;
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
	private CategoryManager categoryManager;

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
	public Showcase getShowcase() {
		return showcase;
	}

	@Override
	public void reloadShowcase() {
		final Session session = sessionFactory.openSession();
		try {
			final Query query = session.createQuery("from billiongoods.server.services.showcase.impl.HibernateShowcaseItem order by pk.section, pk.position asc");

			final List<HibernateShowcaseItem> items = new ArrayList<>();
			for (Object o : query.list()) {
				HibernateShowcaseItem i = (HibernateShowcaseItem) o;
				i.initialize(categoryManager);
				items.add(i);
			}
			showcase = new DefaultShowcase(items);

			for (ShowcaseListener listener : listeners) {
				listener.showcaseInvalidated(showcase);
			}
		} finally {
			session.flush();
			session.close();
		}
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void setCategoryManager(CategoryManager categoryManager) {
		this.categoryManager = categoryManager;
	}
}

