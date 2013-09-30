package billiongoods.core.account;

/**
 * Base account exception that can be thrown by any of {@code AccountManager} methods.
 * <p/>
 * This exception also contains the original player object that caused this exception.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AccountException extends Exception {
	private final Account account;

	public AccountException(String message, Account account) {
		super(message);
		this.account = account;
	}

	public AccountException(String message, Throwable cause, Account account) {
		super(message, cause);
		this.account = account;
	}

	/**
	 * Returns original player object that caused this exception.
	 *
	 * @return the player object.
	 */
	public Account getAccount() {
		return account;
	}
}
