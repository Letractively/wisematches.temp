package billiongoods.core.account;

import billiongoods.core.Language;

import java.util.TimeZone;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AccountEditor {
	private long id = 0;
	private String email;
	private String nickname;
	private TimeZone timeZone = TimeZone.getDefault();
	private Language language = Language.DEFAULT;

	public AccountEditor() {
	}

	public AccountEditor(Account account) {
		this.id = account.getId();
		this.nickname = account.getNickname();
		this.email = account.getEmail();
		this.language = account.getLanguage();
		this.timeZone = account.getTimeZone();
	}

	public AccountEditor(String email, String nickname) {
		this.email = email;
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public AccountEditor setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getNickname() {
		return nickname;
	}

	public AccountEditor setNickname(String nickname) {
		this.nickname = nickname;
		return this;
	}

	public Language getLanguage() {
		return language;
	}

	public AccountEditor setLanguage(Language language) {
		this.language = language;
		return this;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public Account createAccount() {
		if (email == null) {
			throw new IllegalArgumentException("email is not specified");
		}
		if (nickname == null) {
			throw new IllegalArgumentException("nickname is not specified");
		}
		return new AccountDetails(id, email, nickname, language, timeZone);
	}

	/**
	 * Base implementation of {@code Player} interface.
	 *
	 * @author Sergey Klimenko (smklimenko@gmail.com)
	 */
	protected static class AccountDetails extends Account {
		private final String email;
		private final String nickname;
		private final TimeZone timeZone;
		private final Language language;

		private AccountDetails(Long id, String email, String nickname, Language language, TimeZone timeZone) {
			super(id);
			this.email = email;
			this.nickname = nickname;
			this.language = language;
			this.timeZone = timeZone;
		}

		@Override
		public String getEmail() {
			return email;
		}

		@Override
		public String getNickname() {
			return nickname;
		}

		@Override
		public Language getLanguage() {
			return language;
		}

		@Override
		public TimeZone getTimeZone() {
			return timeZone;
		}
	}
}
