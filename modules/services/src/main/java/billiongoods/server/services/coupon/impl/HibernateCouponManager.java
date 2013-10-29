package billiongoods.server.services.coupon.impl;

import billiongoods.server.services.coupon.Coupon;
import billiongoods.server.services.coupon.CouponManager;
import billiongoods.server.services.coupon.CouponType;
import billiongoods.server.services.coupon.ReferenceType;
import billiongoods.server.warehouse.Category;
import billiongoods.server.warehouse.ProductPreview;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateCouponManager implements CouponManager {
	private SessionFactory sessionFactory;

	public HibernateCouponManager() {
	}

	@Override
	public Coupon getCoupon(Integer id) {
		final Session session = sessionFactory.getCurrentSession();
		return (Coupon) session.get(HibernateCoupon.class, id);
	}

	@Override
	public Coupon getCoupon(String code) {
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
	public Coupon createCoupon(String code, double amount, CouponType type, Category category, int count, Date started, Date finished) {
		if (getCoupon(code) != null) {
			return null;
		}

		HibernateCoupon hc = new HibernateCoupon(code, amount, type, category.getId(), ReferenceType.CATEGORY, started, finished, count);
		sessionFactory.getCurrentSession().save(hc);
		return hc;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public Coupon createCoupon(String code, double amount, CouponType type, ProductPreview product, int count, Date started, Date finished) {
		if (getCoupon(code) != null) {
			return null;
		}

		HibernateCoupon hc = new HibernateCoupon(code, amount, type, product.getId(), ReferenceType.PRODUCT, started, finished, count);
		sessionFactory.getCurrentSession().save(hc);
		return hc;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
