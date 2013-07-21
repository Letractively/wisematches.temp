/*
 * Copyright (c) 2011, BillionGoods.
 */
package billiongoods.server.web.servlet.mvc.account;

import billiongoods.core.Language;
import billiongoods.core.Member;
import billiongoods.core.Membership;
import billiongoods.core.personality.DefaultMember;
import billiongoods.core.personality.player.account.*;
import billiongoods.server.services.notify.NotificationException;
import billiongoods.server.services.notify.NotificationSender;
import billiongoods.server.services.notify.NotificationService;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.account.form.AccountRegistrationForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/account")
public class AccountController extends AbstractController {
    private AccountManager accountManager;
    private NotificationService notificationService;

    private static final Logger log = LoggerFactory.getLogger("billiongoods.web.mvc.AccountController");

    public AccountController() {
    }

    /**
     * This is basic form form. Just forward it to appropriate FTL page.
     *
     * @param model the associated model where {@code accountBodyPageName} parameter will be stored.
     * @param form  the form form.
     * @return the FTL full page name without extension
     */
    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String createAccountPage(Model model, @ModelAttribute("registration") AccountRegistrationForm form) {
        model.addAttribute("infoId", "create");
        return "/content/account/create";
    }

    /**
     * This is action publisher for new account. Get model from HTTP POST request and creates new account, if possible.	 *
     *
     * @param model    the all model
     * @param request  original http request
     * @param response original http response
     * @param form     the form request form
     * @param result   the errors errors
     * @param status   the session status. Will be cleared in case of success
     * @return the create account page in case of error of forward to {@code authMember} page in case of success.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String createAccountAction(HttpServletRequest request, HttpServletResponse response,
                                      @Valid @ModelAttribute("registration") AccountRegistrationForm form,
                                      BindingResult result, Model model, SessionStatus status, Locale locale) {
        log.info("Create new account request: {}", form);
        // Validate before next steps
        validateRegistrationForm(form, result, locale);

        Member member = null;
        // Create account if no errors
        if (!result.hasErrors()) {
            try {
                member = new DefaultMember(createAccount(form, request), Membership.BASIC);
            } catch (DuplicateAccountException ex) {
                final Set<String> fieldNames = ex.getFieldNames();
                if (fieldNames.contains("email")) {
                    result.rejectValue("email", "account.register.email.err.busy");
                }
                if (fieldNames.contains("nickname")) {
                    result.rejectValue("nickname", "account.register.nickname.err.busy");
                }
            } catch (InadmissibleUsernameException ex) {
                result.rejectValue("nickname", "account.register.nickname.err.incorrect");
            } catch (Exception ex) {
                log.error("Account can't be created", ex);
                result.reject("billiongoods.error.internal");
            }
        }

        if (result.hasErrors() || member == null) {
            log.info("Account form is not correct: {}", result);
            return createAccountPage(model, form);
        } else {
            log.info("Account has been created.");

            status.setComplete();
            try {
                notificationService.raiseNotification("account.created", member, NotificationSender.ACCOUNTS, null);
            } catch (NotificationException e) {
                log.error("Notification about new account can't be sent", e);
            }
            return forwardToAuthentication(form.getEmail(), form.getPassword(), form.isRememberMe());
        }
    }

    /**
     * This is action publisher for new account. Get model from HTTP POST request and creates new account, if possible.	 *
     *
     * @param request original http request
     * @param form    the form request form
     * @return the create account page in case of error of forward to {@code authMember} page in case of success.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
    @RequestMapping(value = "create.ajax", method = RequestMethod.POST)
    public ServiceResponse createAccountService(HttpServletRequest request,
                                                @Valid @RequestBody AccountRegistrationForm form,
                                                BindingResult result, Model model, Locale locale) {
        log.info("Create new account request (ajax): {}", form);

        if (result.hasErrors()) {
            final FieldError fieldError = result.getFieldError();
            return new ServiceResponse(new ServiceResponse.Failure(fieldError.getCode(), fieldError.getDefaultMessage()));
        }

        if (!form.getPassword().equals(form.getConfirm())) {
            responseFactory.failure("account.register.pwd-cfr.err.mismatch", locale);
        }

        try {
            final Member member = new DefaultMember(createAccount(form, request), Membership.BASIC);
            log.info("Account has been created.");

            try {
                notificationService.raiseNotification("account.created", member, NotificationSender.ACCOUNTS, null);
            } catch (NotificationException e) {
                log.error("Notification about new account can't be sent", e);
            }
            return responseFactory.success(member);
        } catch (DuplicateAccountException ex) {
            final Set<String> fieldNames = ex.getFieldNames();
            if (fieldNames.contains("email")) {
                return responseFactory.failure("account.register.email.err.busy", locale);
            }
            if (fieldNames.contains("nickname")) {
                return responseFactory.failure("account.register.nickname.err.busy", locale);
            }
            return responseFactory.failure("billiongoods.error.internal", locale);
        } catch (InadmissibleUsernameException ex) {
            return responseFactory.failure("account.register.nickname.err.incorrect", locale);
        } catch (Exception ex) {
            log.error("Account can't be created", ex);
            return responseFactory.failure("billiongoods.error.internal", locale);
        }
    }

    /**
     * JSON request for email and username validation.
     *
     * @param email    the email to to checked.
     * @param nickname the nickname to be checked
     * @param result   the bind errors that will be filled in case of any errors.
     * @return the service response that also contains information about errors.
     */
    @RequestMapping(value = "checkAvailability")
    private ServiceResponse getAvailabilityStatus(@RequestParam("email") String email,
                                                  @RequestParam("nickname") String nickname,
                                                  Errors result, Locale locale) {
        log.debug("Check account validation for: {} ('{}')", email, nickname);

        final AccountAvailability a = accountManager.checkAccountAvailable(nickname, email);
        if (a.isAvailable()) {
            return responseFactory.success();
        } else {
            if (!a.isEmailAvailable()) {
                result.rejectValue("email", "account.register.email.err.busy");
            }
            if (!a.isUsernameAvailable()) {
                result.rejectValue("nickname", "account.register.nickname.err.busy");
            }
            if (!a.isUsernameProhibited()) {
                result.rejectValue("nickname", "account.register.nickname.err.incorrect");
            }
            return responseFactory.failure("account.register.err.busy", locale);
        }
    }

    @Autowired
    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    protected static String forwardToAuthentication(final String email, final String password, final boolean rememberMe) {
        try {
            final StringBuilder b = new StringBuilder();
            b.append("j_username=").append(URLEncoder.encode(email, "UTF-8"));
            b.append("&");
            b.append("j_password=").append(URLEncoder.encode(password, "UTF-8"));
            b.append("&");
            b.append("continue=").append("/playground/welcome");
            if (rememberMe) {
                b.append("&").append("rememberMe=true");
            }
            //noinspection SpringMVCViewInspection
            return "forward:/account/loginProcessing?" + b;
        } catch (UnsupportedEncodingException ex) {
            log.error("Very strange exception that mustn't be here", ex);
            //noinspection SpringMVCViewInspection
            return "redirect:/account/login";
        }
    }

    /**
     * Checks that specified form is valid. Otherwise fills specified errors object.
     *
     * @param form   the form to be checked
     * @param errors the binding errors to be filled in case of any error.
     */
    private void validateRegistrationForm(AccountRegistrationForm form, Errors errors, Locale locale) {
        if (!form.getPassword().equals(form.getConfirm())) {
            errors.rejectValue("confirm", "account.register.pwd-cfr.err.mismatch");
        }
        getAvailabilityStatus(form.getEmail(), form.getNickname(), errors, locale);
    }

    /**
     * Creates account based on specified form and returns created user.
     *
     * @param registration the new account form
     * @param request      the original request
     * @return the create player
     * @throws DuplicateAccountException     if account with the same email or nickname already exist
     * @throws InadmissibleUsernameException if nickname can't be used.
     */
    private Account createAccount(AccountRegistrationForm registration, HttpServletRequest request) throws AccountException {
        final AccountEditor editor = new AccountEditor();
        editor.setEmail(registration.getEmail());
        editor.setNickname(registration.getNickname());
        editor.setLanguage(Language.byCode(registration.getLanguage()));
        editor.setTimeZone(Calendar.getInstance(request.getLocale()).getTimeZone());
        return accountManager.createAccount(editor.createAccount(), registration.getPassword());
    }
}
