package billiongoods.server.web.i18n;

import billiongoods.core.Language;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class LanguageChangeInterceptor extends LocaleChangeInterceptor {
    private Language defaultLanguage = Language.DEFAULT;

    public LanguageChangeInterceptor() {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException {
        try {
            return super.preHandle(request, response, handler);
        } catch (IllegalArgumentException ex) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver == null) {
                throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
            }
            localeResolver.setLocale(request, response, defaultLanguage.getLocale());
            return true;
        }
    }

    public void setDefaultLanguage(Language defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }
}
