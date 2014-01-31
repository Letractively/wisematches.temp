package billiongoods.server.web.security.authentication;

import billiongoods.core.account.Account;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AccountAuthenticationToken extends AbstractAuthenticationToken {
	private final Account account;

	public AccountAuthenticationToken(Account account) {
		super(null);
		this.account = account;
	}

	@Override
	public String getName() {
		return account.getEmail();
	}

	@Override
	public Account getPrincipal() {
		return account;
	}

	@Override
	public Object getCredentials() {
		return null;
	}
}
