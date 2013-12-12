package billiongoods.server.web.security;

import billiongoods.core.account.Account;
import billiongoods.core.account.AccountLockManager;
import billiongoods.core.account.AccountManager;
import billiongoods.core.account.AccountRecoveryManager;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetailsService;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MemberDetailsService implements UserDetailsService, SocialUserDetailsService {
	private AccountManager accountManager;
	private AccountLockManager accountLockManager;
	private AccountRecoveryManager accountRecoveryManager;

	public MemberDetailsService() {
	}

	@Override
	public MemberDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return createUserDetails(accountManager.findByEmail(username), username);
	}

	@Override
	public MemberDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataAccessException {
		return createUserDetails(accountManager.getAccount(Long.decode(userId)), userId);
	}

	public MemberDetails loadUserByAccount(Account account) throws UsernameNotFoundException, DataAccessException {
		return createUserDetails(account, null);
	}

	private MemberDetails createUserDetails(Account account, Object id) {
		if (account == null) {
			throw new UsernameNotFoundException("Account not found in the system: " + id);
		}
		final boolean locked = accountLockManager.isAccountLocked(account);
		final boolean expired = (accountRecoveryManager.getToken(account) != null);

		return new MemberDetails(account, locked, expired);
	}

	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public void setAccountLockManager(AccountLockManager accountLockManager) {
		this.accountLockManager = accountLockManager;
	}

	public void setAccountRecoveryManager(AccountRecoveryManager accountRecoveryManager) {
		this.accountRecoveryManager = accountRecoveryManager;
	}
}
