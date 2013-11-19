package billiongoods.server.web.security.web;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is mix of SimpleUrlAuthenticationFailureHandler and ExceptionMappingAuthenticationFailureHandler because
 * both of them can't be extended.
 * <p/>
 * See https://jira.springsource.org/browse/SEC-1821?focusedCommentId=74587#comment-74587 to get more info.
 *
 * @author Sergey Klimenko (smklimenko@gmail.com)
 * @see org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
 * @see org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler
 */
public class WMAuthenticationFailureHandler implements AuthenticationFailureHandler {
	private String failureUrl;
	//	private String defaultFailureUrl;

	private String defaultCode = "system";
	private final Map<Class, String> exceptionCodes = new HashMap<>();

	private boolean allowSessionCreation = true;
	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
//	private final Map<String, String> failureUrlMap = new HashMap<>();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.web.security.AuthenticationFailureHandler");

	public WMAuthenticationFailureHandler() {
	}

	/**
	 * Performs the redirect or forward to the {@code defaultFailureUrl} if set, otherwise returns a 401 error code.
	 * <p/>
	 * If redirecting or forwarding, {@code saveException} will be called to cache the exception for use in
	 * the target view.
	 */
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
										AuthenticationException exception) throws IOException, ServletException {
		String code = getExceptionCode(exception.getClass());
		if (code != null) {
			redirect(request, response, failureUrl + code, exception);
		} else if (defaultCode != null) {
			redirect(request, response, failureUrl + defaultCode, exception);
		} else {
			log.debug("No failure URL set, sending 401 Unauthorized error");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication Failed: " + exception.getMessage());
		}
	}

	protected void redirect(HttpServletRequest request, HttpServletResponse response, String url, AuthenticationException exception) throws IOException {
		saveException(request, exception);
		log.debug("Redirecting to {}", url);
		redirectStrategy.sendRedirect(request, response, url);
	}

	protected void saveException(HttpServletRequest request, AuthenticationException exception) {
		HttpSession session = request.getSession(false);

		if (session != null || allowSessionCreation) {
			request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, exception);
		}
	}

	public void setFailureUrl(String failureUrl) {
		this.failureUrl = failureUrl;
	}

	public void setDefaultCode(String defaultCode) {
		this.defaultCode = defaultCode;
	}

	protected RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}

	protected boolean isAllowSessionCreation() {
		return allowSessionCreation;
	}

	public void setAllowSessionCreation(boolean allowSessionCreation) {
		this.allowSessionCreation = allowSessionCreation;
	}

	public String getExceptionCode(Class clazz) {
		final String s = exceptionCodes.get(clazz);
		if (s == null) {
			return getExceptionCode(clazz.getSuperclass());
		}
		return s;
	}

	public void setExceptionCodes(Map<Class, String> exceptionCodes) {
		this.exceptionCodes.clear();

		for (Map.Entry<Class, String> entry : exceptionCodes.entrySet()) {
			Class clazz = entry.getKey();
			String code = entry.getValue();
			this.exceptionCodes.put(clazz, code);
		}
	}

	public Map<Class, String> getExceptionCodes() {
		return exceptionCodes;
	}
}
