package billiongoods.core.security;

import billiongoods.core.Personality;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public final class PersonalityContext {
	private PersonalityContext() {
	}

	public static Personality getPrincipal() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}

		final Object principal = authentication.getPrincipal();
		if (principal instanceof Personality) {
			return (Personality) principal;
		}
		return null;
	}

	public static boolean hasRole(String role) {
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
