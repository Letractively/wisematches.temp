package billiongoods.core.account;

import java.io.Serializable;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface AccountNicknameLockInfo extends Serializable {
	String getUsername();

	String getReason();
}
