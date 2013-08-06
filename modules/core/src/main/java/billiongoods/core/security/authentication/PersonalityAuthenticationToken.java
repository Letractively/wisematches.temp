package billiongoods.core.security.authentication;

import billiongoods.core.Personality;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class PersonalityAuthenticationToken<P extends Personality> extends AbstractAuthenticationToken {
	private final P personality;

	public PersonalityAuthenticationToken(P personality, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.personality = personality;
		setAuthenticated(personality != null);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public P getPrincipal() {
		return personality;
	}
}
