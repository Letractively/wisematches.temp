package billiongoods.core.account.impl;

import billiongoods.core.account.Account;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of player that contains Hibernate annotations and can be stored into database using Hibernate
 * framework.
 * <p/>
 * This implementation redefines {@code equals} and {@code hashCode} methods and garanties that
 * two players are equals if and only if it's IDs are equals. Any other attributes are ignored.
 *
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
@Entity
@Table(name = "account_personality")
@Cacheable(true)
public class HibernateAccount extends Account {
	@Basic
	@Column(name = "username", nullable = false, length = 100, updatable = false)
	private String username;

	@Basic
	@Column(name = "password", nullable = false, length = 100)
	private String password;

	@Basic
	@Column(name = "email", nullable = false, length = 150)
	private String email;

	@Column(name = "role")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "account_role", joinColumns = @JoinColumn(name = "pid"))
	private Set<String> roles = new HashSet<>();

	/**
	 * Hibernate only constructor
	 */
	HibernateAccount() {
	}

	public HibernateAccount(Account account, String password) {
		super(account.getId());
		this.username = account.getUsername();
		this.password = password;
		updateAccountInfo(account, password);
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public Set<String> getRoles() {
		return roles;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("HibernateAccount");
		sb.append("{id=").append(getId());
		sb.append('}');
		return sb.toString();
	}

	/**
	 * Private method. Allows update player info without creation new object.
	 *
	 * @param account the player with exist
	 */
	final void updateAccountInfo(Account account, String password) {
		if (!this.equals(account)) {
			throw new IllegalArgumentException("Player ids are not equals.");
		}
		if (password != null) {
			this.password = password;
		}
		this.email = account.getEmail();
		this.username = account.getUsername();
	}
}
