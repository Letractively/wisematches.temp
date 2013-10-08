package billiongoods.server.services.notify;

import billiongoods.core.Member;
import billiongoods.core.account.Account;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class Recipient {
	private final String email;
	private final String username;

	public static final Recipient SUPPORT = new Recipient(null, null);
	public static final Recipient MONITORING = new Recipient(null, null);

	public Recipient(String email) {
		this(null, email);
	}

	public Recipient(Member member) {
		this(member.getUsername(), member.getEmail());
	}

	public Recipient(Account account) {
		this(account.getUsername(), account.getEmail());
	}

	private Recipient(String username, String email) {
		this.email = email;
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public String getUsername() {
		return username;
	}

	public boolean isVisitor() {
		return username == null;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Recipient{");
		sb.append("email='").append(email).append('\'');
		sb.append(", username='").append(username).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
