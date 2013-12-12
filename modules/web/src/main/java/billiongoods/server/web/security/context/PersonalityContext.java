package billiongoods.server.web.security.context;

import billiongoods.core.Personality;
import billiongoods.server.web.security.MemberDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.UserIdSource;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class PersonalityContext implements UserIdSource {
	private PersonalityContext() {
	}

	@Override
	public String getUserId() {
		final Personality principal = getPrincipal();
		if (principal == null) {
			return null;
		}
		return String.valueOf(principal.getId());
	}

	public Personality getPrincipal() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}

		final Object principal = authentication.getPrincipal();
		if (principal instanceof MemberDetails) {
			return ((MemberDetails) principal).getMember();
		}

		if (principal instanceof Personality) {
			return (Personality) principal;
		}
		return null;
	}

	public boolean hasRole(String role) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			for (GrantedAuthority authority : authentication.getAuthorities()) {
				if (authority.getAuthority().equals(role)) {
					return true;
				}
			}
		}
		return false;
	}
}
