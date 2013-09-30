package billiongoods.core.account;

/**
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public interface AccountRecoveryManager {
	/**
	 * Generates new recovery token for specified account.
	 *
	 * @param account the account for that new recovery token must be generated.
	 * @return generated recovery token.
	 */
	RecoveryToken generateToken(Account account);


	/**
	 * Returns recovery token for specified account or {@code null} if account doesn't have recovery token or
	 * it's expired.
	 *
	 * @param account the account who token should be returned.
	 * @return cookies token for specified account or <code>null</code> if account has no token.
	 */
	RecoveryToken getToken(Account account);

	/**
	 * Removes recovery token for specified account.
	 *
	 * @param account the account that recovery token must be removed.
	 */
	RecoveryToken clearToken(Account account);
}