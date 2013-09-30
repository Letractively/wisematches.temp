package billiongoods.core.security.authentication;

import billiongoods.core.Visitor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class VisitorAuthenticationProvider implements AuthenticationProvider {
	public VisitorAuthenticationProvider() {
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final AnonymousAuthenticationToken token = (AnonymousAuthenticationToken) authentication;

		final Long id = (Long) token.getPrincipal();
		return new VisitorAuthenticationToken(new Visitor(id));
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return AnonymousAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
