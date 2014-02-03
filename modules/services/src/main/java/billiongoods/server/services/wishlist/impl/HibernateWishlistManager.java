package billiongoods.server.services.wishlist.impl;

import billiongoods.core.Personality;
import billiongoods.core.search.entity.EntitySearchManager;
import billiongoods.server.services.wishlist.WishlistManager;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateWishlistManager extends EntitySearchManager<Integer, Personality, Void> implements WishlistManager {
	public HibernateWishlistManager() {
		super(HibernateWishProduct.class);
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void addWishProducts(Personality person, Integer... productId) {
		final Session session = sessionFactory.getCurrentSession();

		for (Integer integer : productId) {
			HibernateWishProduct p = new HibernateWishProduct(person.getId(), integer);
			session.save(p);
		}
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void removeWishProducts(Personality person, Integer... productId) {
		final Session session = sessionFactory.getCurrentSession();

		final Query query = session.createQuery("delete from billiongoods.server.services.wishlist.impl.HibernateWishProduct p " +
				"where p.pk.person=:person and p.pk.product in :products");
		query.setParameter("person", person.getId());
		query.setParameterList("products", productId, IntegerType.INSTANCE);

		query.executeUpdate();
	}

	@Override
	protected void applyRestrictions(Criteria criteria, Personality context, Void filter) {
		criteria.add(Restrictions.eq("pk.person", context.getId()));
	}

	@Override
	protected void applyProjections(Criteria criteria, Personality context, Void filter) {
		criteria.setProjection(Projections.property("pk.product"));
	}
}
