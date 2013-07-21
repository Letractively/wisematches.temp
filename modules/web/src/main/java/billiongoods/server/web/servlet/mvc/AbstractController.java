package billiongoods.server.web.servlet.mvc;

import billiongoods.core.MessageFormatter;
import billiongoods.core.PersonalityManager;
import billiongoods.core.Player;
import billiongoods.core.security.PersonalityContext;
import billiongoods.server.web.servlet.sdo.ServiceResponseFactory;
import billiongoods.server.web.servlet.view.StaticContentGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public abstract class AbstractController {
    protected MessageFormatter messageSource;
    protected ServiceResponseFactory responseFactory;
    protected PersonalityManager personalityManager;
    protected StaticContentGenerator staticContentGenerator;

    protected AbstractController() {
    }

    @ModelAttribute("title")
    public String getTitle(HttpServletRequest request) {
        final Object title = request.getAttribute("title");
        if (title != null) {
            return String.valueOf(title);
        }
        final String uri = request.getServletPath() + (request.getPathInfo() != null ? request.getPathInfo() : "");
        if (uri.length() <= 1) {
            return "title.default";
        }
        return "title." + uri.replaceAll("/", ".").substring(1);
    }

    @ModelAttribute("principal")
    public Player getPrincipal() {
        return PersonalityContext.getPrincipal();
    }

    @SuppressWarnings("unchecked")
    protected <P extends Player> P getPrincipal(Class<P> type) {
        final Player principal = getPrincipal();
        if (principal == null) {
            throw new AccessDeniedException("unregistered");
        }
        if (!type.isAssignableFrom(principal.getClass())) {
            throw new AccessDeniedException("unregistered");
        }
        return (P) principal;
    }

    protected void setTitleExtension(Model model, String value) {
        model.addAttribute("titleExtension", value);
    }

    @Autowired
    public void setMessageSource(MessageFormatter messageSource) {
        this.messageSource = messageSource;
        this.responseFactory = new ServiceResponseFactory(messageSource);
    }

    @Autowired
    public void setPersonalityManager(PersonalityManager personalityManager) {
        this.personalityManager = personalityManager;
    }

    @Autowired
    public void setStaticContentGenerator(StaticContentGenerator staticContentGenerator) {
        this.staticContentGenerator = staticContentGenerator;
    }
}