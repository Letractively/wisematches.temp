package billiongoods.server.web.security.authentication;

import billiongoods.server.web.security.MemberDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MemberAuthenticationToken extends AbstractAuthenticationToken {
	private final MemberDetails memberDetails;

	public MemberAuthenticationToken(MemberDetails memberDetails) {
		super(memberDetails.getAuthorities());
		this.memberDetails = memberDetails;
		setAuthenticated(true);
	}

	@Override
	public String getName() {
		return memberDetails.getMember().getEmail();
	}

	@Override
	public Object getPrincipal() {
		return memberDetails;
	}

	@Override
	public Object getCredentials() {
		return null;
	}
}
