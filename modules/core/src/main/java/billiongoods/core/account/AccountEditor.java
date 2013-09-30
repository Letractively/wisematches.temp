package billiongoods.core.account;

import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AccountEditor {
	private long id = 0;
	private String email;
	private String username;

	public AccountEditor() {
	}

	public AccountEditor(Account account) {
		this.id = account.getId();
		this.email = account.getEmail();
		this.username = account.getUsername();
	}

	public AccountEditor(String email, String username) {
		this.email = email;
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public AccountEditor setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getUsername() {
		return username;
	}

	public AccountEditor setUsername(String username) {
		this.username = username;
		return this;
	}

	public Account createAccount() {
		if (email == null) {
			throw new IllegalArgumentException("email is not specified");
		}
		if (username == null) {
			throw new IllegalArgumentException("username is not specified");
		}
		return new AccountDetails(id, email, username);
	}

	/**
	 * Base implementation of {@code Player} interface.
	 *
	 * @author Sergey Klimenko (smklimenko@gmail.com)
	 */
	protected static class AccountDetails extends Account {
		private final String email;
		private final String username;

		private AccountDetails(Long id, String email, String username) {
			super(id);
			this.email = email;
			this.username = username;
		}

		@Override
		public String getEmail() {
			return email;
		}

		@Override
		public String getUsername() {
			return username;
		}

		@Override
		public Set<String> getRoles() {
			return null;
		}
	}
}
