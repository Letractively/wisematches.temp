package billiongoods.server.web.servlet.mvc.account;


import billiongoods.core.Player;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.account.form.AccountLoginForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/account")
public class AuthenticationController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger("billiongoods.web.mvc.AuthenticationController");

    public AuthenticationController() {
    }

    /**
     * This is basic login page that shows small information about the site and login form.
     *
     * @param form   the empty login form. Required for FTL page.
     * @param model  the original model
     * @param locale the locale to get a info page
     * @return the appropriate FTL layout page.
     */
    @RequestMapping("login")
    public String loginPage(@ModelAttribute("login") AccountLoginForm form, Model model, Locale locale) {
        enableFullView(model);
        form.setRememberMe("true"); // by default remember me enabled
//		model.addAttribute("", Boolean.TRUE);
        return processLoginPage("general", model, locale);
    }

    @RequestMapping("login.ajax")
    public ServiceResponse loginService(HttpServletResponse response) throws IOException {
        final Player principal = getPrincipal();
        if (principal != null) {
            return responseFactory.success(principal);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return responseFactory.failure();
        }
    }

    @RequestMapping("logout.ajax")
    public ServiceResponse logoutService() {
        final Player principal = getPrincipal();
        if (principal == null) {
            return responseFactory.success();
        } else {
            return responseFactory.failure();
        }
    }

    @RequestMapping("loginAuth")
    public String loginFailedDefault(@ModelAttribute("login") AccountLoginForm form, BindingResult result,
                                     HttpSession session, Model model, Locale locale) {
        return loginFailedCredentials(form, result, session, model, locale);
    }

    @RequestMapping(value = "/loginAuth", params = "error=credential")
    public String loginFailedCredentials(@ModelAttribute("login") AccountLoginForm form, BindingResult result,
                                         HttpSession session, Model model, Locale locale) {
        restoreAccountLoginForm(form, session);
        enableFullView(model);
        result.rejectValue("j_password", "account.login.err.credential");

        return processLoginPage("general", model, locale);
    }

    @RequestMapping(value = "loginAuth", params = "error=session")
    public String loginFailedSession(@ModelAttribute("login") AccountLoginForm form, BindingResult result,
                                     HttpSession session, Model model, Locale locale) {
        restoreAccountLoginForm(form, session);
        enableShortView(form, model, true);

        return processLoginPage("session", model, locale);
    }

    @RequestMapping(value = "loginAuth", params = "error=status")
    public String loginFailedStatus(@ModelAttribute("login") AccountLoginForm form, BindingResult result,
                                    HttpSession session, Model model, Locale locale) {
        restoreAccountLoginForm(form, session);
        enableFullView(model);

        final AuthenticationException ex = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        if (ex instanceof AccountStatusException) {
            if (ex instanceof LockedException) {
                result.rejectValue("j_password", "account.login.err.status.locked");
            } else if (ex instanceof DisabledException) {
                result.rejectValue("j_password", "account.login.err.status.disabled");
            } else if (ex instanceof CredentialsExpiredException) {
                result.rejectValue("j_password", "account.login.err.status.credential");
            } else if (ex instanceof AccountExpiredException) {
                result.rejectValue("j_password", "account.login.err.status.expired");
            }
        }
        return processLoginPage("status", model, locale);
    }

    @RequestMapping(value = "loginAuth", params = "error=insufficient")
    public String loginFailedInsufficient(@ModelAttribute("login") AccountLoginForm form, BindingResult result,
                                          HttpSession session, Model model, Locale locale) {
        restoreAccountLoginForm(form, session);
        enableShortView(form, model, false);

        return processLoginPage("insufficient", model, locale);
    }

    @RequestMapping(value = "loginAuth", params = "error=system")
    public String loginFailedSystem(@ModelAttribute("login") AccountLoginForm form,
                                    HttpSession session, Model model, Locale locale) {
        restoreAccountLoginForm(form, session);
        enableFullView(model);

        final AuthenticationException ex = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        log.error("Unknown authentication exception received for {}", form, ex);

        return processLoginPage("system", model, locale);
    }

    @SuppressWarnings("deprecation")
    private void restoreAccountLoginForm(AccountLoginForm form, HttpSession session) {
        if (form.getJ_username() == null) {
            final Authentication authentication;
            final AuthenticationException ex = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                authentication = ex.getAuthentication();
            } else {
                authentication = SecurityContextHolder.getContext().getAuthentication();
            }
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
                form.setJ_username(authentication.getName());
            }
        }
    }

    private void enableFullView(Model model) {
        model.addAttribute("showRememberMe", Boolean.TRUE);
        model.addAttribute("showRegistration", Boolean.TRUE);
        model.addAttribute("showPredefinedUsername", Boolean.FALSE);
    }

    private void enableShortView(AccountLoginForm form, Model model, Boolean rememberMe) {
        model.addAttribute("showRememberMe", rememberMe);
        model.addAttribute("showRegistration", Boolean.FALSE);
        model.addAttribute("showPredefinedUsername", form.hasUsername());
    }

    private String processLoginPage(String page, Model model, Locale locale) {
        if (!staticContentGenerator.generatePage(page, "general", false, model, locale)) {
            staticContentGenerator.generatePage("general", "general", false, model, locale);
        }
        return "/content/account/general";
    }
}
