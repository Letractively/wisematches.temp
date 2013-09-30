package billiongoods.core.security.authentication;

import billiongoods.core.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MemberAuthenticationToken extends PersonalityAuthenticationToken<Member> {
	public MemberAuthenticationToken(Member personality, Set<String> roles) {
		super(personality, createAuthorities(roles));
	}

	private static Collection<GrantedAuthority> createAuthorities(Set<String> roles) {
		final Collection<GrantedAuthority> res = new ArrayList<>();
		for (String role : roles) {
			res.add(new SimpleGrantedAuthority(role));
		}
		return res;
	}
}
