package billiongoods.server.warehouse.impl;

import billiongoods.server.warehouse.StoreAttribute;
import billiongoods.server.warehouse.StoreAttributeManager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Deprecated
public class HibernateStoreAttributeManager implements StoreAttributeManager, InitializingBean {
	private SessionFactory sessionFactory;
	private final Map<Integer, StoreAttribute> attributeMap = new HashMap<>();

	public HibernateStoreAttributeManager() {
	}

	public void invalidate() {
		attributeMap.clear();

		final Session session = sessionFactory.openSession();
		final Query query = session.createQuery("from billiongoods.server.warehouse.impl.HibernateStoreStoreAttribute");

		final List list = query.list();
		for (Object o : list) {
			final HibernateStoreStoreAttribute a = (HibernateStoreStoreAttribute) o;
			attributeMap.put(a.getId(), a);
			session.evict(a);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		invalidate();
	}

	@Override
	public StoreAttribute getAttribute(Integer id) {
		return attributeMap.get(id);
	}

	@Override
	public Collection<StoreAttribute> getAttributes() {
		return attributeMap.values();
	}

	@Override
	public Collection<StoreAttribute> getAttributes(String name) {
		final List<StoreAttribute> res = new ArrayList<>();
		for (StoreAttribute attribute : attributeMap.values()) {
			if (attribute.getName().equalsIgnoreCase(name)) {
				res.add(attribute);

			}
		}
		return res;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public StoreAttribute createAttribute(String name, String unit) {
		final Session session = sessionFactory.getCurrentSession();
		final HibernateStoreStoreAttribute a = new HibernateStoreStoreAttribute(name, unit);
		session.save(a);
		attributeMap.put(a.getId(), a);
		return a;
	}

	@Override
	public StoreAttribute updateAttribute(Integer id, String name, String unit) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateStoreStoreAttribute ha = (HibernateStoreStoreAttribute) session.get(HibernateStoreStoreAttribute.class, id);
		if (ha == null) {
			throw new IllegalArgumentException("Unknown attribute: " + id);
		}

		ha.setName(name);
		ha.setUnit(unit);
		session.update(ha);
		attributeMap.put(ha.getId(), ha);
		return ha;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
