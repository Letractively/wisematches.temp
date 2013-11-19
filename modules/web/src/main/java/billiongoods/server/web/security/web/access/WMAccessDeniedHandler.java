package billiongoods.server.web.security.web.access;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class WMAccessDeniedHandler implements AccessDeniedHandler {
	private String restrictedErrorPage;
	private String insufficientErrorPage;

	private RequestCache requestCache = new HttpSessionRequestCache();

	private final AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

	public WMAccessDeniedHandler() {
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
		if (!response.isCommitted()) {
			// https://jira.springsource.org/browse/SEC-1773
			requestCache.saveRequest(request, response);

			final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			final boolean rememberMe = authenticationTrustResolver.isRememberMe(auth);
			if (rememberMe && insufficientErrorPage != null) {
				request.setAttribute(WebAttributes.ACCESS_DENIED_403, accessDeniedException);
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				request.getRequestDispatcher(insufficientErrorPage).forward(request, response);
			} else if (restrictedErrorPage != null) {
				request.setAttribute(WebAttributes.ACCESS_DENIED_403, accessDeniedException);
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				request.getRequestDispatcher(restrictedErrorPage).forward(request, response);
			} else {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
			}
		}
	}

	public void setRestrictedErrorPage(String restrictedErrorPage) {
		if ((restrictedErrorPage != null) && !restrictedErrorPage.startsWith("/")) {
			throw new IllegalArgumentException("errorPage must begin with '/'");
		}
		this.restrictedErrorPage = restrictedErrorPage;
	}

	public void setInsufficientErrorPage(String insufficientErrorPage) {
		if ((insufficientErrorPage != null) && !insufficientErrorPage.startsWith("/")) {
			throw new IllegalArgumentException("errorPage must begin with '/'");
		}
		this.insufficientErrorPage = insufficientErrorPage;
	}
}
