package billiongoods.server.services.coupon.impl;

import billiongoods.server.services.coupon.Coupon;
import billiongoods.server.services.coupon.CouponAmountType;
import billiongoods.server.services.coupon.CouponReferenceType;
import billiongoods.server.warehouse.Catalog;
import billiongoods.server.warehouse.ProductPreview;

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

	@Column(name = "creation")
	@Temporal(TemporalType.TIMESTAMP)
	private Date creation;

	@Column(name = "termination")
	@Temporal(TemporalType.TIMESTAMP)
	private Date termination;

	@Column(name = "amount")
	private double amount;

	@Column(name = "amountType")
	@Enumerated(EnumType.ORDINAL)
	private CouponAmountType amountType;

	@Column(name = "reference")
	private Integer reference;

	@Column(name = "referenceType")
	@Enumerated(EnumType.ORDINAL)
	private CouponReferenceType referenceType;

	@Column(name = "utilizedCount")
	private int utilizedCount;

	@Column(name = "allocatedCount")
	private int allocatedCount;

	@Column(name = "lastUtilization")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUtilization;

	@Deprecated
	HibernateCoupon() {
	}

	public HibernateCoupon(String code, double amount, CouponAmountType amountType, Integer reference, CouponReferenceType referenceType, int allocatedCount, Date termination) {
		this.code = code;
		this.creation = new Date();
		this.termination = termination;
		this.amount = amount;
		this.amountType = amountType;
		this.reference = reference;
		this.referenceType = referenceType;
		this.allocatedCount = allocatedCount;
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
	public Date getCreation() {
		return creation;
	}

	@Override
	public Date getTermination() {
		return termination;
	}

	@Override
	public double getAmount() {
		return amount;
	}

	@Override
	public CouponAmountType getAmountType() {
		return amountType;
	}

	@Override
	public Integer getReference() {
		return reference;
	}

	@Override
	public CouponReferenceType getReferenceType() {
		return referenceType;
	}

	@Override
	public int getUtilizedCount() {
		return utilizedCount;
	}

	@Override
	public int getAllocatedCount() {
		return allocatedCount;
	}

	@Override
	public Date getLastUtilization() {
		return lastUtilization;
	}

	@Override
	public boolean isActive() {
		return !isTerminated() && !isFullyUtilized();
	}

	@Override
	public boolean isTerminated() {
		return termination != null && termination.getTime() < System.currentTimeMillis();
	}

	@Override
	public boolean isFullyUtilized() {
		return allocatedCount != 0 && utilizedCount >= allocatedCount;
	}

	@Override
	public double process(ProductPreview product, Catalog catalog) {
		final double pa = product.getPrice().getAmount();
		if (isApplicable(product, catalog)) {
			switch (amountType) {
				case PRICE:
					return pa < amount ? pa : amount;
				case FIXED:
					return pa - amount < 0d ? 0d : pa - amount;
				case PERCENT:
					return pa - pa * (amount / 100.d);
				default:
					throw new IllegalArgumentException("Unsupported coupon type: " + amountType);
			}
		}
		return pa;
	}

	@Override
	public boolean isApplicable(ProductPreview product, Catalog catalog) {
		if (!isActive()) {
			return false;
		}

		if (referenceType == CouponReferenceType.PRODUCT) {
			return product.getId().equals(reference);
		} else if (referenceType == CouponReferenceType.CATEGORY) {
			return catalog.getCategory(reference).isRealKinship(catalog.getCategory(product.getCategoryId()));
		} else {
			throw new IllegalArgumentException("Unsupported reference type: " + referenceType);
		}
	}

	void close() {
		this.termination = new Date();
	}

	void couponUsed(int count) {
		utilizedCount += count;
		lastUtilization = new Date();
	}
}
