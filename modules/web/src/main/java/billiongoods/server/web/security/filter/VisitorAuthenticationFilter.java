package billiongoods.server.web.security.filter;

import billiongoods.core.Language;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class VisitorAuthenticationFilter extends GenericFilterBean implements InitializingBean {
    private String visitorProcessingUrl = DEFAULT_GUEST_PROCESSING_URL;

    private LocaleResolver localeResolver;

    private AuthenticationFailureHandler failureHandle;
    private AuthenticationSuccessHandler successHandler;
    private AuthenticationManager authenticationManager;
    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();

    private static final String DEFAULT_GUEST_PROCESSING_URL = "/j_spring_security_guest";

    private static final List<GrantedAuthority> VISITOR = AuthorityUtils.createAuthorityList("player", "visitor");

    public VisitorAuthenticationFilter() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        if (applyVisitorForThisRequest(request)) {
            try {
                final Language language = Language.byLocale(localeResolver.resolveLocale(request));

                final AnonymousAuthenticationToken visitor = new AnonymousAuthenticationToken("visitor", language, VISITOR);
                visitor.setDetails(authenticationDetailsSource.buildDetails(request));

                final Authentication authResult = authenticationManager.authenticate(visitor);
                SecurityContextHolder.getContext().setAuthentication(authResult);
                successHandler.onAuthenticationSuccess((HttpServletRequest) req, (HttpServletResponse) res, authResult);
            } catch (AuthenticationException ex) {
                SecurityContextHolder.clearContext();
                failureHandle.onAuthenticationFailure((HttpServletRequest) req, (HttpServletResponse) res, ex);
            }
        } else {
            chain.doFilter(req, res);
        }
    }

    protected boolean applyVisitorForThisRequest(HttpServletRequest request) {
        return visitorProcessingUrl.equals(request.getRequestURI());
    }

    public void setLocaleResolver(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    public void setVisitorProcessingUrl(String guestProcessingUrl) {
        Assert.hasText(guestProcessingUrl, "Guest processing url must not be empty or null");
        this.visitorProcessingUrl = guestProcessingUrl;
    }

    public void setFailureHandle(AuthenticationFailureHandler failureHandle) {
        this.failureHandle = failureHandle;
    }

    public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setAuthenticationDetailsSource(AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
    }
}
