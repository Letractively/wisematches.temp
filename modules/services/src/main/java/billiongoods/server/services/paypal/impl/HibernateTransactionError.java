package billiongoods.server.services.paypal.impl;

import billiongoods.server.services.paypal.TransactionPhase;

import javax.persistence.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "paypal_transaction_error")
public class HibernateTransactionError {
	@Id
	@Column(name = "id", nullable = false, updatable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "tnxId", updatable = false)
	private Long tnxId;

	@Column(name = "code", updatable = false)
	private String code;

	@Column(name = "severity", updatable = false)
	private String severity;

	@Column(name = "phase", updatable = false)
	@Enumerated(EnumType.ORDINAL)
	private TransactionPhase phase;

	@Column(name = "shortMessage", updatable = false)
	private String shortMessage;

	@Column(name = "longMessage", updatable = false)
	private String longMessage;

	@Deprecated
	HibernateTransactionError() {
	}

	public HibernateTransactionError(String code, String severity, TransactionPhase phase, String shortMessage, String longMessage) {
		this.code = code;
		this.severity = severity;
		this.phase = phase;
		this.shortMessage = shortMessage;
		this.longMessage = longMessage;
	}

	public String getCode() {
		return code;
	}

	public String getSeverity() {
		return severity;
	}

	public TransactionPhase getPhase() {
		return phase;
	}

	public String getShortMessage() {
		return shortMessage;
	}

	public String getLongMessage() {
		return longMessage;
	}
}
