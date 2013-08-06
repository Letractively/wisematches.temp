package billiongoods.core.security.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MemberAuthenticationProvider implements AuthenticationProvider {
	public MemberAuthenticationProvider() {
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		System.out.println(authentication);

/*
		final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
		final String username = (String) token.getPrincipal();
		final String password = (String) token.getCredentials();
		if (password == null) {
			throw new BadCredentialsException("No password provided");
		}
*/

		// load Member here and parse it.

		return new MemberAuthenticationToken(null);
	}

/*
	@Override
    protected MemberDetails loadValidPersonalityDetails(Authentication authentication) {
        final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;
        final String username = (String) token.getPrincipal();
        final String password = (String) token.getCredentials();
        if (password == null) {
            throw new BadCredentialsException("No password provided");
        }

        final MemberDetailsService detailsService = getPersonalityDetailsService();
        final MemberDetails details = detailsService.loadMemberByEmail(username);
        if (details == null) {
            throw new UsernameNotFoundException("User is unknown: " + username);
        }

        if (!detailsService.isPasswordValid(details, password)) {
            throw new BadCredentialsException("Passwords mismatch");
        }
        return details;
    }
*/


	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication) ||
				RememberMeAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
