package billiongoods.core.account;

/**
 * The exception is thrown if user nickname can't be used according to User Naming Policy.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class InadmissibleUsernameException extends AccountException {
	private final String reason;

	public InadmissibleUsernameException(Account account, String reason) {
		super("InadmissibleUsername: " + account.getUsername(), account);
		this.reason = reason;
	}

	/**
	 * Returns original reason.
	 *
	 * @return the prohibit reason.
	 */
	public String getReason() {
		return reason;
	}
}
