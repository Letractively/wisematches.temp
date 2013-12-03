package billiongoods.server.web.security.web.rememberme;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;

/**
 * FIX FOR: https://jira.springsource.org/browse/SEC-1964
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class WMRememberMeServices extends PersistentTokenBasedRememberMeServices {
	private WMPersistentTokenRepository tokenRepository;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.security.RememberMeService");

	public WMRememberMeServices(String key, UserDetailsService userDetailsService, WMPersistentTokenRepository tokenRepository) {
		super(key, userDetailsService, tokenRepository);
		this.tokenRepository = tokenRepository;
	}

	@Override
	protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
		if (super.rememberMeRequested(request, parameter)) {
			return true;
		}

		Object paramValue = request.getAttribute(parameter);
		if (paramValue != null && paramValue instanceof Boolean) {
			request.removeAttribute(parameter);
			return (Boolean) paramValue;
		}
		return false;
	}

	@Override
	protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {

		if (cookieTokens.length != 2) {
			throw new InvalidCookieException("Cookie token did not contain " + 2 +
					" tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
		}

		final String presentedSeries = cookieTokens[0];
		final String presentedToken = cookieTokens[1];

		PersistentRememberMeToken token = tokenRepository.getTokenForSeries(presentedSeries);

		if (token == null) {
			// No series match, so we can't authenticate using this cookie
			tokenRepository.removeToken(presentedSeries);
			throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
		}

		// We have a match for this user/series combination
		if (!presentedToken.equals(token.getTokenValue())) {
			// Token doesn't match series value. Delete all logins for this user and throw an exception to warn them.
			tokenRepository.removeToken(token.getSeries());

			throw new CookieTheftException(messages.getMessage("PersistentTokenBasedRememberMeServices.cookieStolen",
					"Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack."));
		}

		if (token.getDate().getTime() + getTokenValiditySeconds() * 1000L < System.currentTimeMillis()) {
			tokenRepository.removeToken(token.getSeries());
			throw new RememberMeAuthenticationException("Remember-me login has expired");
		}

		// Token also matches, so login is valid. Update the token value, keeping the *same* series number.
		if (log.isDebugEnabled()) {
			log.debug("Refreshing persistent login token for user '" + token.getUsername() + "', series '" +
					token.getSeries() + "'");
		}

		PersistentRememberMeToken newToken = new PersistentRememberMeToken(token.getUsername(),
				token.getSeries(), generateTokenData(), new Date());

		try {
			tokenRepository.updateToken(newToken.getSeries(), newToken.getTokenValue(), newToken.getDate());
			addCookie(newToken, request, response);
		} catch (DataAccessException e) {
			log.error("Failed to update token: ", e);
			throw new RememberMeAuthenticationException("Autologin failed due to data access problem");
		}

		return getUserDetailsService().loadUserByUsername(token.getUsername());
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		// Get remember me cookie before next steps
		final String rememberMeCookie = extractRememberMeCookie(request);

		// Copied from AbstractRememberMeServices.logout. The super.logout(request, response, authentication);
		// can't be used because tokenRepository.removeUserTokens is invoked
		if (log.isDebugEnabled()) {
			log.debug("Logout of user "
					+ (authentication == null ? "Unknown" : authentication.getName()));
		}
		cancelCookie(request, response);

		// if has cookie: decode and remove only this one
		if (rememberMeCookie != null) {
			final String[] strings = decodeCookie(rememberMeCookie);
			tokenRepository.removeToken(strings[0]);
		}
	}

	private void addCookie(PersistentRememberMeToken token, HttpServletRequest request, HttpServletResponse response) {
		setCookie(new String[]{token.getSeries(), token.getTokenValue()}, getTokenValiditySeconds(), request, response);
	}
}