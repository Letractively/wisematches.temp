package billiongoods.server.services.payment.impl;

import billiongoods.server.services.payment.OrderLog;
import billiongoods.server.services.payment.OrderState;

import javax.persistence.*;
import java.util.Date;

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

	@Column(name = "timestamp", updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@Column(name = "parameter", updatable = false)
	private String parameter;

	@Column(name = "commentary", updatable = false)
	private String commentary;

	@Column(name = "orderState", updatable = false)
	@Enumerated(EnumType.ORDINAL)
	private OrderState orderState;

	@Deprecated
	HibernateOrderLog() {
	}

	public HibernateOrderLog(Long orderId, String parameter, String commentary, OrderState orderState) {
		this.orderId = orderId;
		this.timestamp = new Date();
		this.parameter = parameter;
		this.commentary = commentary;
		this.orderState = orderState;
	}

	@Override
	public Date getTimeStamp() {
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
		sb.append(", state=").append(orderState);
		sb.append(", timestamp=").append(timestamp);
		sb.append(", parameter='").append(parameter).append('\'');
		sb.append(", commentary='").append(commentary).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
