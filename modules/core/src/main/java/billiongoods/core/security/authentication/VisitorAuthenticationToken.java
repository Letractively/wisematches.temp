package billiongoods.core.security.authentication;

import billiongoods.core.Visitor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class VisitorAuthenticationToken extends PersonalityAuthenticationToken<Visitor> {
	public VisitorAuthenticationToken(Visitor personality) {
		super(personality, Collections.singleton(new SimpleGrantedAuthority("visitor")));
	}
}
