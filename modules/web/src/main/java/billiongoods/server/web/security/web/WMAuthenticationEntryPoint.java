package billiongoods.server.web.security.web;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class WMAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private WMAuthenticationFailureHandler authenticationFailureHandler;

	public WMAuthenticationEntryPoint() {
	}

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		if (authException == null) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} else {
			final String exceptionCode = authenticationFailureHandler.getExceptionCode(authException.getClass());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exceptionCode);
		}
	}

	public void setAuthenticationFailureHandler(WMAuthenticationFailureHandler authenticationFailureHandler) {
		this.authenticationFailureHandler = authenticationFailureHandler;
	}
}
