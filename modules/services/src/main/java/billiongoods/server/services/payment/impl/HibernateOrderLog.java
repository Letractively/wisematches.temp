package billiongoods.server.services.payment.impl;

import billiongoods.server.services.payment.OrderLog;
import billiongoods.server.services.payment.OrderState;
import billiongoods.server.services.payment.Parcel;
import billiongoods.server.services.payment.ParcelState;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_order_log")
public class HibernateOrderLog implements OrderLog {
	@Id
	@Column(name = "id", updatable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "orderId", updatable = false)
	private Long orderId;

	@Column(name = "orderState", updatable = false)
	@Enumerated(EnumType.ORDINAL)
	private OrderState orderState;

	@Column(name = "parcelId", updatable = false)
	private Long parcelId;

	@Column(name = "parcelState", updatable = false)
	@Enumerated(EnumType.ORDINAL)
	private ParcelState parcelState;

	@Column(name = "parameter", updatable = false)
	private String parameter;

	@Column(name = "commentary", updatable = false)
	private String commentary;

	@Column(name = "timestamp", updatable = false)
	private LocalDateTime timestamp;

	@Deprecated
	HibernateOrderLog() {
	}

	public HibernateOrderLog(HibernateOrder order, String parameter, String commentary) {
		this(order, null, parameter, commentary);
	}

	public HibernateOrderLog(HibernateOrder order, Parcel parcel, String parameter, String commentary) {
		this.orderId = order.getId();
		this.orderState = order.getState();

		if (parcel != null) {
			this.parcelId = parcel.getId();
			this.parcelState = parcel.getState();
		}

		this.parameter = parameter;
		this.commentary = commentary;

		this.timestamp = LocalDateTime.now();
	}


	@Override
	public Long getOrderId() {
		return orderId;
	}

	@Override
	public boolean isOrderChange() {
		return !isParcelChange();
	}

	@Override
	public boolean isParcelChange() {
		return parcelId != null;
	}

	@Override
	public ParcelState getParcelState() {
		return parcelState;
	}

	@Override
	public Long getParcelId() {
		return parcelId;
	}

	@Override
	public LocalDateTime getTimeStamp() {
		return timestamp;
	}

	@Override
	public String getParameter() {
		return parameter;
	}

	@Override
	public String getCommentary() {
		return commentary;
	}

	@Override
	public OrderState getOrderState() {
		return orderState;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateOrderLog{");
		sb.append("id=").append(id);
		sb.append(", orderId=").append(orderId);
		sb.append(", parcelId=").append(parcelId);
		sb.append(", timestamp=").append(timestamp);
		sb.append(", parameter='").append(parameter).append('\'');
		sb.append(", commentary='").append(commentary).append('\'');
		sb.append(", orderState=").append(orderState);
		sb.append(", parcelState=").append(parcelState);
		sb.append('}');
		return sb.toString();
	}
}
