package billiongoods.server.web.security.authentication;

import billiongoods.core.account.AccountManager;
import billiongoods.server.web.security.MemberDetails;
import billiongoods.server.web.security.MemberDetailsService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsChecker;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MemberAuthenticationProvider implements AuthenticationProvider {
	private AccountManager accountManager;
	private UserDetailsChecker userDetailsChecker;
	private MemberDetailsService memberDetailsService;

	public MemberAuthenticationProvider() {
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final MemberDetails details;

		if (authentication instanceof UsernamePasswordAuthenticationToken) {
			details = getUsernamePasswordDetails((UsernamePasswordAuthenticationToken) authentication);
		} else if (authentication instanceof AccountAuthenticationToken) {
			details = getMemberDetails((AccountAuthenticationToken) authentication);
		} else {
			throw new InternalAuthenticationServiceException("Unsupported authentication: " + authentication);
		}

		userDetailsChecker.check(details);

		return new MemberAuthenticationToken(details);
	}

	private MemberDetails getMemberDetails(AccountAuthenticationToken token) {
		return memberDetailsService.loadUserByAccount(token.getPrincipal());
	}

	private MemberDetails getUsernamePasswordDetails(UsernamePasswordAuthenticationToken token) {
		final String username = (String) token.getPrincipal();
		final String password = (String) token.getCredentials();
		if (password == null) {
			throw new BadCredentialsException("No password provided");
		}
		final MemberDetails details = memberDetailsService.loadUserByUsername(username);
		if (!accountManager.validateCredentials(details.getId(), password)) {
			throw new BadCredentialsException("Passwords mismatch");
		}
		return details;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication) ||
				AccountAuthenticationToken.class.isAssignableFrom(authentication);
	}

	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	public void setUserDetailsChecker(UserDetailsChecker userDetailsChecker) {
		this.userDetailsChecker = userDetailsChecker;
	}

	public void setMemberDetailsService(MemberDetailsService memberDetailsService) {
		this.memberDetailsService = memberDetailsService;
	}
}
