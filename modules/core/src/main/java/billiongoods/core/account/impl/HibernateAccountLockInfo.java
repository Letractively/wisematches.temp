package billiongoods.core.account.impl;

import billiongoods.core.account.Account;
import billiongoods.core.account.AccountLockInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
@Entity
@Table(name = "account_lock")
class HibernateAccountLockInfo implements AccountLockInfo {
	@Transient
	private Account account;

	@Id
	@Column(name = "account")
	private long accountId;

	@Column(name = "publicReason")
	private String publicReason;

	@Column(name = "privateReason")
	private String privateReason;

	@Column(name = "lockDate")
	private Date lockDate;

	@Column(name = "unlockDate")
	private Date unlockDate;

	/**
	 * Constructor for Hibernate
	 */
	HibernateAccountLockInfo() {
	}

	HibernateAccountLockInfo(Account account, String publicReason, String privateReason, Date unlockDate) {
		this.account = account;
		this.accountId = account.getId();
		this.publicReason = publicReason;
		this.privateReason = privateReason;
		this.lockDate = new Date();
		this.unlockDate = unlockDate;
	}

	@Override
	public Account getAccount() {
		return account;
	}

	@Override
	public String getPublicReason() {
		return publicReason;
	}

	@Override
	public String getPrivateReason() {
		return privateReason;
	}

	@Override
	public Date getLockDate() {
		return lockDate;
	}

	@Override
	public Date getUnlockDate() {
		return unlockDate;
	}


	void setAccount(Account account) {
		this.account = account;
		this.accountId = account.getId();
	}

	void setPublicReason(String publicReason) {
		this.publicReason = publicReason;
	}

	void setPrivateReason(String privateReason) {
		this.privateReason = privateReason;
	}

	void setLockDate(Date lockDate) {
		this.lockDate = lockDate;
	}

	void setUnlockDate(Date unlockDate) {
		this.unlockDate = unlockDate;
	}

	@Override
	public String toString() {
		return "LockAccountInfo{" +
				"account=" + account +
				", playerId=" + accountId +
				", publicReason='" + publicReason + '\'' +
				", privateReason='" + privateReason + '\'' +
				", lockDate=" + lockDate +
				", unlockDate=" + unlockDate +
				'}';
	}
}
