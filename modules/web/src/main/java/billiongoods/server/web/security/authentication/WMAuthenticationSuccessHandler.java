package billiongoods.server.web.security.authentication;

import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class WMAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public WMAuthenticationSuccessHandler() {
    }
}
