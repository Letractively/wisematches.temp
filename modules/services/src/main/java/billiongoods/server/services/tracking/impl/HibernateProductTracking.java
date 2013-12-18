package billiongoods.server.services.tracking.impl;

import billiongoods.server.services.tracking.ProductTracking;
import billiongoods.server.services.tracking.TrackingPerson;
import billiongoods.server.services.tracking.TrackingType;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_product_tracking")
public class HibernateProductTracking implements ProductTracking {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "registration")
	@Temporal(TemporalType.TIMESTAMP)
	private Date registration;

	@Column(name = "personId")
	private Long personId;

	@Column(name = "productId")
	private Integer productId;

	@Column(name = "personEmail")
	private String personEmail;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "type")
	private TrackingType trackingType;

	@Deprecated
	HibernateProductTracking() {
	}

	public HibernateProductTracking(Integer productId, TrackingPerson tracker, TrackingType trackingType) {
		this.productId = productId;
		if (tracker instanceof TrackingPerson.Member) {
			final TrackingPerson.Member member = (TrackingPerson.Member) tracker;
			this.personId = member.getPersonId();
		} else if (tracker instanceof TrackingPerson.Visitor) {
			final TrackingPerson.Visitor member = (TrackingPerson.Visitor) tracker;
			this.personEmail = member.getPersonEmail();
		} else {
			throw new IllegalArgumentException("Incorrect tracker type: " + tracker);
		}
		this.trackingType = trackingType;
		this.registration = new Date();
	}

	Integer getId() {
		return id;
	}

	@Override
	public Date getRegistration() {
		return registration;
	}

	@Override
	public Integer getProductId() {
		return productId;
	}

	@Override
	public Long getPersonId() {
		return personId;
	}

	@Override
	public String getPersonEmail() {
		return personEmail;
	}

	@Override
	public TrackingType getTrackingType() {
		return trackingType;
	}
}
