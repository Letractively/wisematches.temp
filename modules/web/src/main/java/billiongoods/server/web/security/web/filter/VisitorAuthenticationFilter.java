package billiongoods.server.web.security.web.filter;

import billiongoods.core.Visitor;
import billiongoods.server.web.security.web.filter.visitor.SessionVisitorServices;
import billiongoods.server.web.security.web.filter.visitor.VisitorServices;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class VisitorAuthenticationFilter extends GenericFilterBean {
	private long lastGeneratedVisitorId;

	private AuthenticationManager authenticationManager;
	private VisitorServices visitorServices = new SessionVisitorServices();

	private static final Set<SimpleGrantedAuthority> DEFAULT_AUTHORITIES = Collections.singleton(new SimpleGrantedAuthority("visitor"));

	public VisitorAuthenticationFilter() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			final HttpServletRequest req = (HttpServletRequest) request;
			final HttpServletResponse res = (HttpServletResponse) response;

			Long visitorId = visitorServices.loadVisitorId(req, res);
			if (visitorId == null) {
				visitorId = createVisitorId();
				visitorServices.saveVisitorId(visitorId, req, res);
			}

			final AnonymousAuthenticationToken authentication = new AnonymousAuthenticationToken("visitor", new Visitor(visitorId), DEFAULT_AUTHORITIES);
			final Authentication authenticate = authenticationManager.authenticate(authentication);

			SecurityContextHolder.getContext().setAuthentication(authenticate);
		}

		chain.doFilter(request, response);
	}

	private Long createVisitorId() {
		return System.nanoTime();
	}

	public void setVisitorServices(VisitorServices visitorServices) {
		this.visitorServices = visitorServices;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
}
