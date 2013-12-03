package billiongoods.server.web.security.web.filter.visitor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public interface VisitorServices {
	static final String VISITOR_KEY = "VisitorId"; // Cookie name

	Long loadVisitorId(HttpServletRequest request, HttpServletResponse response);

	void saveVisitorId(Long id, HttpServletRequest request, HttpServletResponse response);
}
