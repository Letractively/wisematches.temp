package billiongoods.core.account;

import billiongoods.core.Passport;

/**
 * <code>AccountManager</code> allows create and authentificate players or does search by some criteria.
 * <p/>
 * {@code AccountManager} does not extends {@code PersonalityManager} because {@code PersonalityManager} has a lot
 * of implementation.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface AccountManager {
	void addAccountListener(AccountListener l);

	void removeAccountListener(AccountListener l);


	/**
	 * Returns player by it's id.
	 *
	 * @param id the player's id.
	 * @return the player by it's id or <code>null</code> if player is unknown.
	 */
	Account getAccount(Long id);

	/**
	 * Searches a player by specified email.
	 *
	 * @param email the email to be searched
	 * @return the found player or {@code null} if no player with specified email.
	 */
	Account findByEmail(String email);


	/**
	 * Creates new player based on information in specified player object. This method does not
	 * modify original object and returns new object instead.
	 *
	 * @param email    the email address for the account
	 * @param password the password for the account.
	 * @param passport the passport of the account
	 * @return created player object possible the same but can be new one so returned object must be
	 * used after this method was called.
	 * @throws DuplicateAccountException     if account with specified username or email already exist.
	 * @throws InadmissibleUsernameException if select username can't be used by User Naming Policy
	 */
	Account createAccount(String email, String password, Passport passport) throws DuplicateAccountException, InadmissibleUsernameException;


	Account updateEmail(Account account, String email) throws UnknownAccountException, DuplicateAccountException;

	Account updatePassword(Account account, String password) throws UnknownAccountException;

	Account updatePassport(Account account, Passport password) throws UnknownAccountException, DuplicateAccountException, InadmissibleUsernameException;


	/**
	 * Checks that password of specified account if equals with specified {@code raw password}. Depends
	 * on internal implementation raw password can be encoded before comparation.
	 *
	 * @param id       the account to be checked.
	 * @param password original raw password for comparation
	 * @return valid account if password is valid for specified account or {@code null} if not.
	 */
	boolean validateCredentials(Long id, String password);

	/**
	 * Checks is account with specified username and email can be created or not.
	 *
	 * @param username the username to be checked
	 * @param email    the email to be checked.
	 * @return the {@code AccountAvailability} object that contains information about availability.
	 */
	AccountAvailability validateAvailability(String username, String email);


	/**
	 * Removes specified player.
	 *
	 * @param account the player to be removed.
	 * @return removed account.
	 * @throws UnknownAccountException if an account for specified player is unknown.
	 */
	Account removeAccount(Account account) throws UnknownAccountException;
}