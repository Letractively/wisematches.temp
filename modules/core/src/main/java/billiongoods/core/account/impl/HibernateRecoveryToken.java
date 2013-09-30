package billiongoods.core.account.impl;

import billiongoods.core.account.Account;
import billiongoods.core.account.RecoveryToken;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "account_recovery")
public class HibernateRecoveryToken implements RecoveryToken {
	@Id
	@Column(name = "account")
	public long account;

	@Column(name = "token", nullable = false)
	public String token;

	@Basic
	@Column(name = "generated", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date generated;

	HibernateRecoveryToken() {
	}

	public HibernateRecoveryToken(Account account, String token, Date generated) {
		this.account = account.getId();
		this.token = token;
		this.generated = generated;
	}

	@Override
	public String getToken() {
		return token;
	}

	@Override
	public Date getGenerated() {
		return generated;
	}

	@Override
	public String toString() {
		return "Token{playerId=" + account + ", token=" + token + ", date=" + generated + "}";
	}
}