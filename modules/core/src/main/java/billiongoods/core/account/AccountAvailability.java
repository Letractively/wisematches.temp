package billiongoods.core.account;

import java.io.Serializable;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class AccountAvailability implements Serializable {
	private final boolean usernameAvailable;
	private final boolean emailAvailable;
	private final boolean usernameProhibited;

	public AccountAvailability(boolean emailAvailable, boolean usernameAvailable, boolean usernameProhibited) {
		this.usernameAvailable = usernameAvailable;
		this.emailAvailable = emailAvailable;
		this.usernameProhibited = usernameProhibited;
	}

	public boolean isUsernameAvailable() {
		return usernameAvailable;
	}

	public boolean isUsernameProhibited() {
		return usernameProhibited;
	}

	public boolean isEmailAvailable() {
		return emailAvailable;
	}

	public boolean isAvailable() {
		return usernameAvailable && emailAvailable;
	}
}
