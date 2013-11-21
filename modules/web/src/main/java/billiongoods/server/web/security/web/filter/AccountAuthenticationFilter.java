package billiongoods.server.web.security.web.filter;

import billiongoods.core.account.Account;
import billiongoods.server.web.security.authentication.AccountAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AccountAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
	private String principalRequestAttribute = "PRE_AUTHENTICATED_ACCOUNT";
	private boolean exceptionIfHeaderMissing = true;

	public AccountAuthenticationFilter() {
		super("/authorization");
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
		final Object attribute = request.getAttribute(principalRequestAttribute);
		if (attribute == null && exceptionIfHeaderMissing) {
			throw new AuthenticationServiceException(principalRequestAttribute + " attribute not found in request.");
		}
		request.removeAttribute(principalRequestAttribute);

		final AccountAuthenticationToken accountAuthenticationToken = new AccountAuthenticationToken((Account) attribute);
		accountAuthenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));
		return getAuthenticationManager().authenticate(accountAuthenticationToken);
	}

	public void setPrincipalRequestAttribute(String principalRequestAttribute) {
		this.principalRequestAttribute = principalRequestAttribute;
	}

	public void setExceptionIfHeaderMissing(boolean exceptionIfHeaderMissing) {
		this.exceptionIfHeaderMissing = exceptionIfHeaderMissing;
	}
}
