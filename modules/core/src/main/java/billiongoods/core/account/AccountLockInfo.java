package billiongoods.core.account;

import java.io.Serializable;
import java.util.Date;

/**
 * Information about lock of account. Who, when and why locked the player.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface AccountLockInfo extends Serializable {
	Account getAccount();

	String getPublicReason();

	String getPrivateReason();

	Date getLockDate();

	Date getUnlockDate();
}
