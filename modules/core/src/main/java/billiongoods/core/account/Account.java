package billiongoods.core.account;

import billiongoods.core.Passport;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

/**
 * The {@code Player} interface represents simple user. This interface is read-only
 * and contains only methods that returns common and necessary information about the player,
 * like id, username, nickname, email and language.
 * <p/>
 * Please note that this interface declares only information that is required every time when
 * player info should be shown. Any other player information, like gender, city and so on is defined in
 * {@code PlayerProfile} object.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@MappedSuperclass
public abstract class Account implements Serializable {
	@Id
	@Column(name = "id", nullable = false, updatable = false, unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	protected Account() {
	}

	protected Account(Long id) {
		this.id = id;
	}


	public Long getId() {
		return id;
	}

	/**
	 * Returns a email of the player.
	 * <p/>
	 * This value can't be null.
	 *
	 * @return not null email of the player.
	 */
	public abstract String getEmail();

	/**
	 * Returns passport for this account.
	 *
	 * @return the passport for this account.
	 */
	public abstract Passport getPassport();

	/**
	 * Returns all account roles.
	 *
	 * @return roles set or {@code null} or empty list if there are no special roles.
	 */
	public abstract Set<String> getRoles();

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Account)) return false;

		Account account = (Account) o;
		return id.equals(account.id);
	}
}