package billiongoods.core.account.impl;

import billiongoods.core.Passport;
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
//	@Basic
//	@Column(name = "username", nullable = false, length = 100, updatable = false)
//	private String username;

	@Basic
	@Column(name = "email", nullable = false, length = 150)
	private String email;

	@Basic
	@Column(name = "password", nullable = false, length = 100)
	private String password;

	@Embedded
	private Passport passport;

	@Column(name = "role")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "account_role", joinColumns = @JoinColumn(name = "account"))
	private Set<String> roles = new HashSet<>();

	/**
	 * Hibernate only constructor
	 */
	HibernateAccount() {
	}

	public HibernateAccount(String email, String password, Passport passport) {
		this.email = email;
		this.password = password;
		this.passport = passport;
	}

	@Override
	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public Passport getPassport() {
		return passport;
	}

	@Override
	public Set<String> getRoles() {
		return roles;
	}

	void setEmail(String email) {
		this.email = email;
	}

	void setPassword(String password) {
		this.password = password;
	}

	void setPassport(Passport passport) {
		this.passport = passport;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("HibernateAccount");
		sb.append("{id=").append(getId());
		sb.append('}');
		return sb.toString();
	}
}
