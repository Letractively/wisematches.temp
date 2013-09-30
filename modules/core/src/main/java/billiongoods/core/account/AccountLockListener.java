package billiongoods.core.account;

import java.util.Date;

/**
 * @author <a href="mailto:smklimenko@gmail.com">Sergey Klimenko</a>
 */
public interface AccountLockListener {
	void accountLocked(Account player, String publicReason, String privateReason, Date unlockdate);

	void accountUnlocked(Account player);
}
