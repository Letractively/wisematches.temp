package billiongoods.core.security.authentication;

import billiongoods.core.Member;
import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MemberAuthenticationToken extends PersonalityAuthenticationToken<Member> {
	public MemberAuthenticationToken(Member personality) {
		super(personality, createAuthorities(personality));
	}

	private static Collection<GrantedAuthority> createAuthorities(Member member) {
		final Collection<GrantedAuthority> res = new ArrayList<>();
		return res;
	}


}
