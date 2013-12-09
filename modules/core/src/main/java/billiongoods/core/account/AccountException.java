package billiongoods.core.account;

/**
 * Base account exception that can be thrown by any of {@code AccountManager} methods.
 * <p/>
 * This exception also contains the original player object that caused this exception.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AccountException extends Exception {
	public AccountException(String message) {
		super(message);
	}

	public AccountException(String message, Throwable cause) {
		super(message, cause);
	}
}
