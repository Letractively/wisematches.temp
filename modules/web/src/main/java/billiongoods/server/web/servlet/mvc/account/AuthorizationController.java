/*
 * Copyright (c) 2011, BillionGoods.
 */
package billiongoods.server.web.servlet.mvc.account;

import billiongoods.core.account.*;
import billiongoods.server.services.notify.NotificationException;
import billiongoods.server.services.notify.NotificationService;
import billiongoods.server.services.notify.Recipient;
import billiongoods.server.services.notify.Sender;
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
import org.springframework.ui.Model;
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
	private NotificationService notificationService;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.web.mvc.AccountController");

	public AuthorizationController() {
	}

	@ModelAttribute("hideNavigation")
	public boolean isHideNavigation() {
		return true;
	}

	@RequestMapping("login")
	public String loginPage(@ModelAttribute("login") AccountLoginForm login,
							@ModelAttribute("registration") AccountRegistrationForm register, Model model) {
		hideNavigation(model);
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
			try {
				notificationService.raiseNotification(Recipient.get(account), Sender.ACCOUNTS, "account.created", account, account.getUsername());
			} catch (NotificationException e) {
				log.error("Notification about new account can't be sent", e);
			}
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
			b.append("continue=").append("/");
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

	@Autowired
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}
}
