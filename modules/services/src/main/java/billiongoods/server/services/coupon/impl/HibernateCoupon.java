package billiongoods.server.services.coupon.impl;

import billiongoods.server.services.coupon.Coupon;
import billiongoods.server.services.coupon.CouponType;
import billiongoods.server.services.coupon.ReferenceType;
import billiongoods.server.warehouse.Catalog;
import billiongoods.server.warehouse.ProductDescription;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_coupon")
public class HibernateCoupon implements Coupon {
	@Id
	@Column(name = "id", nullable = false, updatable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "code", nullable = false, updatable = false, unique = true)
	private String code;

	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	@Column(name = "closure")
	@Temporal(TemporalType.TIMESTAMP)
	private Date closure;

	@Column(name = "amount")
	private double amount;

	@Column(name = "couponType")
	@Enumerated(EnumType.ORDINAL)
	private CouponType couponType;

	@Column(name = "referenceId")
	private Integer referenceId;

	@Column(name = "referenceType")
	@Enumerated(EnumType.ORDINAL)
	private ReferenceType referenceType;

	@Column(name = "started")
	@Temporal(TemporalType.TIMESTAMP)
	private Date started;

	@Column(name = "finished")
	@Temporal(TemporalType.TIMESTAMP)
	private Date finished;

	@Column(name = "scheduledCount")
	private int scheduledCount;

	@Column(name = "remainingCount")
	private int remainingCount;

	@Deprecated
	HibernateCoupon() {
	}

	public HibernateCoupon(String code, double amount, CouponType couponType, Integer referenceId, ReferenceType referenceType, int scheduledCount) {
		this(code, amount, couponType, referenceId, referenceType, null, null, scheduledCount);
	}

	public HibernateCoupon(String code, double amount, CouponType couponType, Integer referenceId, ReferenceType referenceType, Date started, Date finished) {
		this(code, amount, couponType, referenceId, referenceType, started, finished, 0);
	}

	public HibernateCoupon(String code, double amount, CouponType couponType, Integer referenceId, ReferenceType referenceType, Date started, Date finished, int scheduledCount) {
		this.code = code;
		this.amount = amount;
		this.couponType = couponType;
		this.referenceId = referenceId;
		this.referenceType = referenceType;
		this.finished = finished;
		this.started = started;
		this.created = new Date();
		this.scheduledCount = this.remainingCount = scheduledCount;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public Date getCreated() {
		return created;
	}

	@Override
	public Date getClosure() {
		return closure;
	}

	@Override
	public double getAmount() {
		return amount;
	}

	@Override
	public CouponType getCouponType() {
		return couponType;
	}

	@Override
	public Integer getReferenceId() {
		return referenceId;
	}

	@Override
	public ReferenceType getReferenceType() {
		return referenceType;
	}

	@Override
	public Date getStarted() {
		return started;
	}

	@Override
	public Date getFinished() {
		return finished;
	}

	@Override
	public int getScheduledCount() {
		return scheduledCount;
	}

	@Override
	public int getRemainingCount() {
		return remainingCount;
	}

	void close() {
		this.closure = new Date();
	}

	void usedCoupons(int count) {
		remainingCount = remainingCount - count;
	}

	@Override
	public boolean isActive() {
		if (closure != null) {
			return false;
		}
		if (scheduledCount != 0 && remainingCount == 0) {
			return false;
		}
		if (started != null && started.getTime() > System.currentTimeMillis()) { // future
			return false;
		}
		if (finished != null && finished.getTime() < System.currentTimeMillis()) { // history
			return false;
		}
		return true;
	}

	@Override
	public double process(ProductDescription product, Catalog catalog) {
		final double pa = product.getPrice().getAmount();
		if (isApplicable(product, catalog)) {
			switch (couponType) {
				case PRICE:
					return pa < amount ? pa : amount;
				case FIXED:
					return pa - amount < 0d ? 0d : pa - amount;
				case PERCENT:
					return pa - pa * (amount / 100.d);
				default:
					throw new IllegalArgumentException("Unsupported coupon type: " + couponType);
			}
		}
		return pa;
	}

	@Override
	public boolean isApplicable(ProductDescription product, Catalog catalog) {
		if (!isActive()) {
			return false;
		}

		if (referenceType == ReferenceType.PRODUCT) {
			return product.getId().equals(referenceId);
		} else if (referenceType == ReferenceType.CATEGORY) {
			return catalog.getCategory(referenceId).isRealKinship(catalog.getCategory(product.getCategoryId()));
		} else {
			throw new IllegalArgumentException("Unsupported reference type: " + referenceType);
		}
	}
}
