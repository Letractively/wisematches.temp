package billiongoods.server.web.security;

import billiongoods.core.account.AccountManager;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MemberAuthenticationProvider implements AuthenticationProvider {
	private AccountManager accountManager;
	private MemberDetailsService memberDetailsService;

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

		final MemberDetails memberDetails = memberDetailsService.loadUserByUsername(username);
		if (!memberDetails.isAccountNonLocked()) {
			throw new LockedException("Account is locked");
		}

		if (!memberDetails.isCredentialsNonExpired()) {
			throw new AccountExpiredException("Expired");
		}

		if (!accountManager.checkAccountCredentials(memberDetails.getId(), password)) {
			throw new BadCredentialsException("Passwords mismatch");
		}
		return new UsernamePasswordAuthenticationToken(memberDetails, null, memberDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public void setMemberDetailsService(MemberDetailsService memberDetailsService) {
		this.memberDetailsService = memberDetailsService;
	}
}
