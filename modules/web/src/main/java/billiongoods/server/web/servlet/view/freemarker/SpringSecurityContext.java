package billiongoods.server.web.servlet.view.freemarker;

import billiongoods.core.security.PersonalityContext;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class SpringSecurityContext {
    public SpringSecurityContext() {
    }

    public boolean hasRole(String role) {
        return PersonalityContext.hasRole(role);
    }
}
