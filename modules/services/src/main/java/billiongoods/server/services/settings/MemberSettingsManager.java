package billiongoods.server.services.settings;

import billiongoods.core.account.Account;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface MemberSettingsManager {
	MemberSettings getMemberSettings(Account account);

	void setMemberSettings(Account account, MemberSettings settings);
}
