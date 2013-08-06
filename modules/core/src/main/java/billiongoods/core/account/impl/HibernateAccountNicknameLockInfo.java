package billiongoods.core.account.impl;

import billiongoods.core.account.AccountNicknameLockInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
@Entity
@Table(name = "account_blacknames")
class HibernateAccountNicknameLockInfo implements AccountNicknameLockInfo {
	@Id
	@Column(name = "username", length = 100, nullable = false)
	private String username;

	@Column(name = "reason", length = 255, nullable = false)
	private String reason;

	/**
	 * For hibernate
	 */
	HibernateAccountNicknameLockInfo() {
	}

	HibernateAccountNicknameLockInfo(String username, String reason) {
		this.username = username;
		this.reason = reason;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getReason() {
		return reason;
	}

	void setUsername(String username) {
		this.username = username;
	}

	void setReason(String reason) {
		this.reason = reason;
	}
}
