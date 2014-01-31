package billiongoods.server.web.security;

import billiongoods.core.Member;
import billiongoods.core.account.Account;
import billiongoods.core.secure.MemberContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.social.security.SocialUserDetails;

import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MemberDetails implements MemberContainer, SocialUserDetails {
	private final Member member;
	private final boolean locked;
	private final boolean expired;
	private final Collection<GrantedAuthority> authorities;

	private static final Map<String, SimpleGrantedAuthority> cache = new HashMap<>();
	private static final Set<GrantedAuthority> GRANTED_AUTHORITY_SET = Collections.<GrantedAuthority>singleton(new SimpleGrantedAuthority("member"));

	public MemberDetails(Account account, boolean locked, boolean expired) {
		this.locked = locked;
		this.expired = expired;
		this.member = new Member(account.getId(), account.getEmail(), account.getPassport());
		this.authorities = createAuthorities(account.getRoles());
	}

	public Long getId() {
		return member.getId();
	}

	@Override
	public String getUserId() {
		return String.valueOf(member.getId());
	}

	@Override
	public Member getMember() {
		return member;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getEmail() {
		return member.getEmail();
	}

	@Override
	public String getUsername() {
		return member.getPassport().getUsername();
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public boolean isEnabled() {
		return !locked && !expired;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !locked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !expired;
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
}
