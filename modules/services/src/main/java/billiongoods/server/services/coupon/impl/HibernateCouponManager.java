package billiongoods.server.services.coupon.impl;

import billiongoods.core.search.entity.EntitySearchManager;
import billiongoods.server.services.coupon.*;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.ProductPreview;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateCouponManager extends EntitySearchManager<Coupon, CouponContext, Void> implements CouponManager {
	public HibernateCouponManager() {
		super(HibernateCoupon.class);
	}

	@Override
	public Coupon getCoupon(Integer id) {
		if (id == null) {
			return null;
		}
		final Session session = sessionFactory.getCurrentSession();
		return (Coupon) session.get(HibernateCoupon.class, id);
	}

	@Override
	public Coupon getCoupon(String code) {
		if (code == null) {
			return null;
		}
		final Session session = sessionFactory.getCurrentSession();

		final Criteria criteria = session.createCriteria(HibernateCoupon.class);
		criteria.add(Restrictions.eq("code", code));
		return (Coupon) criteria.uniqueResult();
	}

	@Override
	public Coupon closeCoupon(Integer id) {
		final Session session = sessionFactory.getCurrentSession();

		final HibernateCoupon hc = (HibernateCoupon) session.get(HibernateCoupon.class, id);
		if (hc == null) {
			return null;
		}
		hc.close();
		session.update(hc);
		return hc;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Coupon createCoupon(String code, double amount, CouponAmountType amountType, Category category, int count, Date termination) {
		if (getCoupon(code) != null) {
			return null;
		}

		HibernateCoupon hc = new HibernateCoupon(code, amount, amountType, category.getId(), CouponReferenceType.CATEGORY, count, termination);
		sessionFactory.getCurrentSession().save(hc);
		return hc;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Coupon createCoupon(String code, double amount, CouponAmountType amountType, ProductPreview product, int count, Date termination) {
		if (getCoupon(code) != null) {
			return null;
		}

		HibernateCoupon hc = new HibernateCoupon(code, amount, amountType, product.getId(), CouponReferenceType.PRODUCT, count, termination);
		sessionFactory.getCurrentSession().save(hc);
		return hc;
	}

	@Override
	protected void applyRestrictions(Criteria criteria, CouponContext context, Void filter) {
		if (context != null) {
			if (context.getReference() != null && context.getReferenceType() != null) {
				criteria.add(Restrictions.eq("reference", context.getReference()));
				criteria.add(Restrictions.eq("referenceType", context.getReferenceType()));
			}
		}
	}

	@Override
	protected void applyProjections(Criteria criteria, CouponContext context, Void filter) {
	}
}
