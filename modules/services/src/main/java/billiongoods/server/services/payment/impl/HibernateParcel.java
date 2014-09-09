package billiongoods.server.services.payment.impl;

import billiongoods.server.services.payment.OrderItem;
import billiongoods.server.services.payment.Parcel;
import billiongoods.server.services.payment.ParcelState;
import billiongoods.server.services.payment.Timeline;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_order_parcel")
public class HibernateParcel implements Parcel {
	@javax.persistence.Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "orderId", updatable = false)
	private Long orderId;

	@Column(name = "number", updatable = false)
	private int number;

	@Column(name = "exceptedResume")
	private LocalDateTime exceptedResume;

	@Column(name = "commentary", length = 255)
	private String commentary;

	@Column(name = "refundToken", length = 45)
	private String refundToken;

	@Embedded
	private HibernateTimeline timeline;

	@Column(name = "chinaMailTracking", length = 45)
	private String chinaMailTracking;

	@Column(name = "internationalTracking", length = 45)
	private String internationalTracking;


	@Column(name = "state")
	@Enumerated(EnumType.ORDINAL)
	private ParcelState state = ParcelState.PROCESSING;

	@Deprecated
	HibernateParcel() {
	}

	public HibernateParcel(HibernateOrder order, int number) {
		this.number = number;
		this.orderId = order.getId();

		final Timeline orderTimeline = order.getTimeline();

		this.timeline = new HibernateTimeline(LocalDateTime.from(orderTimeline.getCreated()));
		this.timeline.setStarted(LocalDateTime.from(orderTimeline.getStarted()));
		this.timeline.setProcessed(LocalDateTime.now());
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public int getNumber() {
		return number;
	}


	@Override
	public Timeline getTimeline() {
		return timeline;
	}

	@Override
	public ParcelState getState() {
		return state;
	}

	@Override
	public String getRefundToken() {
		return refundToken;
	}


	@Override
	public LocalDateTime getExpectedResume() {
		return exceptedResume;
	}

	@Override
	public boolean isTracking() {
		return internationalTracking != null && !internationalTracking.isEmpty();
	}

	@Override
	public String getChinaMailTracking() {
		return chinaMailTracking;
	}

	@Override
	public String getInternationalTracking() {
		return internationalTracking;
	}

	@Override
	public boolean contains(OrderItem item) {
		final HibernateOrderItem i = (HibernateOrderItem) item;
		return id != null && id.equals(i.getParcelId());
	}

	void shipping(String tracking) {
		this.chinaMailTracking = tracking;
		updateParcelState(ParcelState.SHIPPING);
	}

	void shipped(String tracking) {
		this.internationalTracking = tracking;
		updateParcelState(ParcelState.SHIPPED);
	}

	void closed(LocalDateTime deliveryDate) {
		timeline.setFinished(deliveryDate);

		updateParcelState(ParcelState.CLOSED);
	}

	void cancel() {
		updateParcelState(ParcelState.CANCELLED);
	}

	void suspend(LocalDateTime resume) {
		this.exceptedResume = resume;

		updateParcelState(ParcelState.SUSPENDED);
	}

	private void updateParcelState(ParcelState state) {
		ParcelState oldState = this.state;

		this.state = state;

		if (oldState != state) {
			updateTimeline(state);
		}
	}

	private void updateTimeline(ParcelState state) {
		switch (state) {
			case SHIPPED:
			case SHIPPING:
				if (timeline.getShipped() == null) {
					timeline.setShipped(LocalDateTime.now());
				}
				break;
			case CANCELLED:
				if (timeline.getFinished() == null) {
					timeline.setFinished(LocalDateTime.now());
				}
				break;
		}
	}
}
