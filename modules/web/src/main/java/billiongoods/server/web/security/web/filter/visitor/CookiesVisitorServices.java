package billiongoods.server.web.security.web.filter.visitor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class CookiesVisitorServices implements VisitorServices {
	private static final int VISITOR_MAX_AGE = 60 * 60 * 24 * 7; // in seconds, one week

	public CookiesVisitorServices() {
	}

	@Override
	public Long loadVisitorId(HttpServletRequest request, HttpServletResponse response) {
		final Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(VISITOR_KEY)) {
					try {
						return Long.parseLong(cookie.getValue());
					} catch (NumberFormatException ex) {
						return null;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void saveVisitorId(Long id, HttpServletRequest request, HttpServletResponse response) {
		final Cookie cookie = new Cookie(VISITOR_KEY, id.toString());
		cookie.setMaxAge(VISITOR_MAX_AGE);

		response.addCookie(cookie);
	}
}
