package billiongoods.core.account;

/**
 * The {@code AccountListener} notifies about new accounts or removed players.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface AccountListener {
	/**
	 * Indicates that new account has been created.
	 *
	 * @param account the created player account.
	 */
	void accountCreated(Account account);

	/**
	 * Indicates that account has been removed.
	 *
	 * @param account the removed account.
	 */
	void accountRemove(Account account);

	/**
	 * Indicates that account has been updated.
	 *
	 * @param oldAccount the old account
	 * @param newAccount the new account
	 */
	void accountUpdated(Account oldAccount, Account newAccount);
}
