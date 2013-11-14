package billiongoods.core.security.authentication;

import billiongoods.core.Member;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MemberAuthenticationToken extends AbstractAuthenticationToken {
	private final Member personality;

	private static final Map<String, SimpleGrantedAuthority> cache = new HashMap<>();

	private static final Set<GrantedAuthority> GRANTED_AUTHORITY_SET = Collections.<GrantedAuthority>singleton(new SimpleGrantedAuthority("member"));


	public MemberAuthenticationToken(Member personality, Set<String> roles) {
		super(createAuthorities(roles));
		this.personality = personality;
		setAuthenticated(personality != null);
	}

	private static Collection<GrantedAuthority> createAuthorities(Set<String> roles) {
		if (roles == null) {
			return GRANTED_AUTHORITY_SET;
		}
		final Collection<GrantedAuthority> res = new ArrayList<>(roles.size() + GRANTED_AUTHORITY_SET.size());
		for (String role : roles) {
			SimpleGrantedAuthority authority = cache.get(role);
			if (authority == null) {
				authority = new SimpleGrantedAuthority(role);
				cache.put(role, authority);
			}
			res.add(authority);
		}
		res.addAll(GRANTED_AUTHORITY_SET);
		return res;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return personality;
	}
}
