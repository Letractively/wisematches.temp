package billiongoods.server.web.security.web.filter.visitor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class SessionVisitorServices implements VisitorServices {
	public SessionVisitorServices() {
	}

	@Override
	public Long loadVisitorId(HttpServletRequest request, HttpServletResponse response) {
		final HttpSession session = request.getSession();
		if (session != null) {
			return (Long) session.getAttribute(VISITOR_KEY);
		}
		return null;
	}

	@Override
	public void saveVisitorId(Long id, HttpServletRequest request, HttpServletResponse response) {
		final HttpSession session = request.getSession(true);
		session.setAttribute(VISITOR_KEY, id);
	}
}
