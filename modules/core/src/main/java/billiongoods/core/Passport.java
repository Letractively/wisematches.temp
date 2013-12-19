package billiongoods.core;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.util.TimeZone;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Embeddable
public class Passport implements Serializable {
	@Column(name = "username")
	private String username;

	@Column(name = "language")
	@Enumerated(EnumType.STRING)
	private Language language;

	@Column(name = "timezone")
	private TimeZone timeZone;

	@Deprecated
	Passport() {
	}

	public Passport(String username) {
		this(username, Language.DEFAULT, TimeZone.getDefault());
	}

	public Passport(String username, Language language, TimeZone timeZone) {
		this.username = username;
		this.language = language;
		this.timeZone = timeZone;
	}

	/**
	 * Returns username of a person
	 *
	 * @return the username of a person
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Returns primary language for a person
	 *
	 * @return the primary language for a person
	 */
	public Language getLanguage() {
		return language;
	}

	/**
	 * Returns timezone for a person.
	 *
	 * @return the timezone for a person.
	 */
	public TimeZone getTimeZone() {
		return timeZone;
	}


	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("HibernatePassport{");
		sb.append("username='").append(username).append('\'');
		sb.append(", language=").append(language);
		sb.append(", timeZone=").append(timeZone);
		sb.append('}');
		return sb.toString();
	}
}
