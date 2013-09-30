package billiongoods.core.account;

import java.util.Date;

/**
 * <code>AccountLockManager</code> allows check is the account is locked or not. This manager also allows
 * lock and unlock account.
 *
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public interface AccountLockManager {
	void addAccountLockListener(AccountLockListener l);

	void removeAccountLockListener(AccountLockListener l);


	void addAccountNicknameLockListener(AccountNicknameLockListener l);

	void removeAccountNicknameLockListener(AccountNicknameLockListener l);


	/**
	 * Locks account of specified player by some time or forever. Any lock required two type of reasone:
	 * <i>public reason</i> that will be shown the player and other players and <i>private reason</i> that
	 * can be shown only moderators.
	 * <p/>
	 * If player already locked the reasones and unlock date will be updated to specified.
	 *
	 * @param player        the player to be locked.
	 * @param publicReason  the public reason that will be shown the player and sign in.
	 * @param privateReason the private reason that will be shown moderators.
	 * @param unlockDate    the unlock date or <code>null</code> if player is locked forever.
	 */
	void lockAccount(Account player, String publicReason, String privateReason, Date unlockDate);

	/**
	 * Unlocks specified player.
	 *
	 * @param player the player to be unlocked.
	 */
	void unlockAccount(Account player);

	/**
	 * Checks that player is locked or not. It's fast operation. If your need get some information about lock
	 * please use {@link #getAccountLockInfo(Account)} method instead.
	 *
	 * @param player the player to be checked.
	 * @return <code>true</code> if player is locked; <code>false</code> - otherwise.
	 * @see #getAccountLockInfo(Account)
	 */
	boolean isAccountLocked(Account player);

	/**
	 * Returns lock information about account. This is heavy operation and it's better to use
	 * {@link #isAccountLocked(Account)} check before call this method.
	 *
	 * @param player the player
	 * @return the information about lock cause or <code>null</code> if player isn't locked.
	 * @see #isAccountLocked(Account)
	 */
	AccountLockInfo getAccountLockInfo(Account player);

	/**
	 * Validates specified nickname is it locked or not.
	 *
	 * @param nickname the nickname to be checked.
	 * @return the reason why nickname is locked or <code>null</code> if nickname isn't locked.
	 */
	String isNicknameLocked(String nickname);

	/**
	 * Locks the <code>nickname</code> with specified <code>reason</code>.
	 *
	 * @param nickname the nickname to be locked
	 * @param reason   the reason why nickname is locked.
	 */
	void lockNickname(String nickname, String reason);

	/**
	 * Unlocks the <code>nickname</code>.
	 *
	 * @param nickname the nickname to be unlocked.
	 */
	void unlockNickname(String nickname);
}