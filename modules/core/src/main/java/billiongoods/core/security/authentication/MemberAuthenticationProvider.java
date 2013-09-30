package billiongoods.core.security.authentication;

import billiongoods.core.Member;
import billiongoods.core.account.Account;
import billiongoods.core.account.AccountLockManager;
import billiongoods.core.account.AccountManager;
import billiongoods.core.account.AccountRecoveryManager;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MemberAuthenticationProvider implements AuthenticationProvider {
	private AccountManager accountManager;
	private AccountLockManager accountLockManager;
	private AccountRecoveryManager accountRecoveryManager;

	public MemberAuthenticationProvider() {
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
		final String username = (String) token.getPrincipal();
		final String password = (String) token.getCredentials();
		if (password == null) {
			throw new BadCredentialsException("No password provided");
		}

		final Account account = accountManager.findByEmail(username);
		if (account == null) {
			throw new UsernameNotFoundException("Account with email " + username + " not found in the system");
		}

		final boolean locked = accountLockManager.isAccountLocked(account);
		if (locked) {
			throw new LockedException("Account is locked");
		}

		final boolean expired = (accountRecoveryManager.getToken(account) != null);
		if (expired) {
			throw new AccountExpiredException("Expired");
		}

		if (!accountManager.checkAccountCredentials(account.getId(), password)) {
			throw new BadCredentialsException("Passwords mismatch");
		}
		return new MemberAuthenticationToken(new Member(account.getId(), account.getUsername(), account.getEmail()), account.getRoles());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication) ||
				RememberMeAuthenticationToken.class.isAssignableFrom(authentication);
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
