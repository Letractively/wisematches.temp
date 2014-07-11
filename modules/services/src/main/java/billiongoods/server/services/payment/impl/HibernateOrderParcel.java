package billiongoods.server.services.payment.impl;

import billiongoods.server.services.payment.OrderItem;
import billiongoods.server.services.payment.OrderParcel;
import billiongoods.server.services.payment.ParcelState;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_order_parcel")
public class HibernateOrderParcel implements OrderParcel {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "number")
	private int number;

	@Column(name = "orderId")
	private Long orderId;

	@Column(name = "exceptedResume")
	@Temporal(TemporalType.TIMESTAMP)
	private Date exceptedResume;

	@Column(name = "commentary", length = 255)
	private String commentary;

	@Column(name = "refundToken", length = 45)
	private String refundToken;


	@Column(name = "timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@Column(name = "chinaMailTracking", length = 45)
	private String chinaMailTracking;

	@Column(name = "internationalTracking", length = 45)
	private String internationalTracking;

	@Column(name = "state")
	@Enumerated(EnumType.ORDINAL)
	private ParcelState state = ParcelState.PROCESSING;

	@Deprecated
	HibernateOrderParcel() {
	}

	public HibernateOrderParcel(HibernateOrder order, int number) {
		this.number = number;
		this.orderId = order.getId();

		this.timestamp = new Date();
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
	public ParcelState getState() {
		return state;
	}


	@Override
	public String getRefundToken() {
		return refundToken;
	}

	@Override
	public Date getExpectedResume() {
		return exceptedResume;
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
	public Date getTimestamp() {
		return timestamp;
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

	void closed() {
		updateParcelState(ParcelState.CLOSED);
	}

	private void updateParcelState(ParcelState state) {
		this.state = state;
		this.timestamp = new Date();
	}
}
