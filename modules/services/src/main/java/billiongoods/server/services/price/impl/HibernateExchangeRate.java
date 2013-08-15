package billiongoods.server.services.price.impl;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "store_exchange")
public class HibernateExchangeRate {
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(name = "timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@Column(name = "exchangeRate")
	private float exchangeRate;

	public HibernateExchangeRate() {
	}

	public HibernateExchangeRate(float exchangeRate) {
		this.timestamp = new Date();
		this.exchangeRate = exchangeRate;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public float getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(float exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernateExchangeRate{");
		sb.append("id=").append(id);
		sb.append(", timestamp=").append(timestamp);
		sb.append(", exchangeRate=").append(exchangeRate);
		sb.append('}');
		return sb.toString();
	}
}
