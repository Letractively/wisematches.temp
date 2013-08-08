/*
 * Copyright (c) 2011, BillionGoods.
 */
package billiongoods.server.web.servlet.mvc.account;

import billiongoods.core.account.*;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.account.form.AccountLoginForm;
import billiongoods.server.web.servlet.mvc.account.form.AccountRegistrationForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Set;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/account")
public class AuthorizationController extends AbstractController {
	private AccountManager accountManager;
//    private NotificationService notificationService;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.web.mvc.AccountController");

	public AuthorizationController() {
	}

	@RequestMapping("login")
	public String loginPage(@ModelAttribute("login") AccountLoginForm login,
							@ModelAttribute("registration") AccountRegistrationForm register) {


		return "/content/account/authorization";
	}

	@RequestMapping("loginAuth")
	public String loginFailed(@ModelAttribute("registration") AccountRegistrationForm form,
							  @ModelAttribute("login") AccountLoginForm login, BindingResult result, HttpSession session) {
		restoreAccountLoginForm(login, session);

		final String error = login.getError();

		switch (error) {
			case "credential":
				result.rejectValue("j_password", "account.login.err.credential");
				break;
			case "status": {
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
				break;
			}
			case "system": {
				final AuthenticationException ex = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
				log.error("Unknown authentication exception received for {}", login, ex);
				break;
			}
		}
		return "/content/account/authorization";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
	@RequestMapping(value = "create", method = RequestMethod.POST)
	public String createAccount(@ModelAttribute("login") AccountLoginForm login,
								@Valid @ModelAttribute("registration") AccountRegistrationForm form,
								BindingResult result, SessionStatus status, Locale locale) {
		log.info("Create new account request: {}", form);
		// Validate before next steps
		validateAccount(form, result, locale);

		Account account = null;
		if (!result.hasErrors()) {
			try {
				account = createAccount(form);
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

		if (result.hasErrors() || account == null) {
			log.info("Account form is not correct: {}", result);
			return "/content/account/authorization";
		} else {
			log.info("Account has been created.");

			status.setComplete();
			return forwardToAuthentication(form.getEmail(), form.getPassword(), form.isRememberMe());
		}
	}

	@RequestMapping(value = "checkAvailability")
	private ServiceResponse checkAvailability(@RequestParam("email") String email,
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
			if (!a.isUsernameProhibited()) {
				result.rejectValue("nickname", "account.register.nickname.err.incorrect");
			}
			return responseFactory.failure("account.register.err.busy", locale);
		}
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


	private void validateAccount(AccountRegistrationForm form, Errors errors, Locale locale) {
		if (!form.getPassword().equals(form.getConfirm())) {
			errors.rejectValue("confirm", "account.register.pwd-cfr.err.mismatch");
		}
		checkAvailability(form.getEmail(), form.getUsername(), errors, locale);
	}

	private Account createAccount(AccountRegistrationForm registration) throws AccountException {
		final AccountEditor editor = new AccountEditor();
		editor.setEmail(registration.getEmail());
		editor.setUsername(registration.getUsername());
		return accountManager.createAccount(editor.createAccount(), registration.getPassword());
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

	@Autowired
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	/**
	 * This is basic form form. Just forward it to appropriate FTL page.
	 *
	 * @param model the associated model where {@code accountBodyPageName} parameter will be stored.
	 * @param form  the form form.
	 * @return the FTL full page name without extension
	 *//*

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String createAccountPage(Model model, @ModelAttribute("registration") AccountRegistrationForm form) {
        model.addAttribute("infoId", "create");
        return "/content/account/create";
    }

    */
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
 *//*


    */
/**
 * This is action publisher for new account. Get model from HTTP POST request and creates new account, if possible.	 *
 *
 * @param request original http request
 * @param form    the form request form
 * @return the create account page in case of error of forward to {@code authMember} page in case of success.
 *//*

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

    */
/**
 * JSON request for email and username validation.
 *
 * @param email    the email to to checked.
 * @param nickname the nickname to be checked
 * @param result   the bind errors that will be filled in case of any errors.
 * @return the service response that also contains information about errors.
 *//*


    @Autowired
    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }



    */
}
