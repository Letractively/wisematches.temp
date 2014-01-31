package billiongoods.server.web.servlet.mvc.account;

import billiongoods.core.Language;
import billiongoods.core.Passport;
import billiongoods.core.Visitor;
import billiongoods.core.account.*;
import billiongoods.server.services.ServerDescriptor;
import billiongoods.server.services.notify.NotificationException;
import billiongoods.server.services.notify.NotificationService;
import billiongoods.server.services.notify.Recipient;
import billiongoods.server.services.notify.Sender;
import billiongoods.server.web.security.MemberDetails;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.account.form.AccountLoginForm;
import billiongoods.server.web.servlet.mvc.account.form.AccountRegistrationForm;
import billiongoods.server.web.servlet.mvc.account.form.SocialAssociationForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ConnectSupport;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.social.security.SocialAuthenticationServiceLocator;
import org.springframework.social.security.provider.SocialAuthenticationService;
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
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

import javax.validation.Valid;
import java.util.*;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/account")
public class AccountController extends AbstractController {
	private ConnectSupport connectSupport;

	private AccountManager accountManager;
	private NotificationService notificationService;

	private UsersConnectionRepository usersConnectionRepository;
	private SocialAuthenticationServiceLocator authenticationServiceLocator;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.web.mvc.AccountSocialController");

	public AccountController() {
		super(true, false);
	}

	@RequestMapping(value = {"", "/", "/create"}, method = RequestMethod.GET)
	public String mainAccountPage() {
		return "redirect:/account/signin";
	}

	@RequestMapping("/signin")
	public String signinInternal(@ModelAttribute("login") AccountLoginForm login, BindingResult result,
								 @ModelAttribute("registration") AccountRegistrationForm register,
								 Model model, NativeWebRequest request) {
		restoreAccountLoginForm(login, request);

		final String error = login.getError();
		if (error != null) {
			switch (error) {
				case "credential":
					result.rejectValue("j_password", "account.signin.err.status.credential");
					break;
				case "status": {
					final AuthenticationException ex = (AuthenticationException) request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, RequestAttributes.SCOPE_SESSION);
					if (ex instanceof AccountStatusException) {
						if (ex instanceof LockedException) {
							result.rejectValue("j_password", "account.signin.err.status.locked");
						} else if (ex instanceof DisabledException) {
							result.rejectValue("j_password", "account.signin.err.status.disabled");
						} else if (ex instanceof CredentialsExpiredException) {
							result.rejectValue("j_password", "account.signin.err.status.credential");
						} else if (ex instanceof AccountExpiredException) {
							result.rejectValue("j_password", "account.signin.err.status.expired");
						}
					}
					break;
				}
				case "system": {
					final AuthenticationException ex = (AuthenticationException) request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, RequestAttributes.SCOPE_SESSION);
					log.error("Unknown authentication exception received for {}", login, ex);
					break;
				}
			}
		}
		model.addAttribute("socialProviders", authenticationServiceLocator.registeredAuthenticationProviderIds());
		return "/content/account/authorization";
	}

	@RequestMapping(value = "check")
	public ServiceResponse checkAvailability(@RequestParam("email") String email,
											 @RequestParam("nickname") String nickname,
											 Errors result, Locale locale) {
		log.debug("Check account validation for: {} ('{}')", email, nickname);

		final AccountAvailability a = accountManager.validateAvailability(nickname, email);
		if (a.isAvailable()) {
			return responseFactory.success();
		} else {
			if (result != null && !a.isEmailAvailable()) {
				result.rejectValue("email", "account.register.email.err.busy");
			}
			if (result != null && !a.isUsernameProhibited()) {
				result.rejectValue("nickname", "account.register.nickname.err.incorrect");
			}
			return responseFactory.failure("account.register.err.busy", locale);
		}
	}

	@RequestMapping("/social/start")
	public String socialStart(NativeWebRequest request) {
		final String provider = request.getParameter("provider");
		if (!authenticationServiceLocator.registeredProviderIds().contains(provider)) {
			throw new IllegalStateException("Unsupported provider: " + provider);
		}
		final SocialAuthenticationService<?> authenticationService = authenticationServiceLocator.getAuthenticationService(provider);
		if (authenticationService == null) {
			throw new ProviderNotFoundException(provider);
		}
		return "redirect:" + connectSupport.buildOAuthUrl(authenticationService.getConnectionFactory(), request);
	}

	@RequestMapping(value = "/social/association", method = RequestMethod.GET)
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
	public String socialAssociation(@ModelAttribute("form") SocialAssociationForm form, Model model, NativeWebRequest request) {
		final ProviderSignInAttempt attempt = (ProviderSignInAttempt) request.getAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);
		if (attempt == null) {
			return "redirect:/account/social/finish";
		}

		final Connection<?> connection = attempt.getConnection();
		final UserProfile userProfile = connection.fetchUserProfile();
		final String email = userProfile.getEmail();

		if (email != null && !email.isEmpty()) {
			final Account account = accountManager.findByEmail(email);
			if (account != null) {
				addAccountAssociation(account, connection);
				return forwardToAuthorization(request, account, true, form.getFinish());
			}
		}

		final List<String> registeredUserIds = usersConnectionRepository.findUserIdsWithConnection(attempt.getConnection());
		final List<Account> accounts = new ArrayList<>(registeredUserIds.size());
		if (registeredUserIds.size() > 1) {
			for (String registeredUserId : registeredUserIds) {
				accounts.add(accountManager.getAccount(Long.decode(registeredUserId)));
			}
		}

		model.addAttribute("plain", Boolean.TRUE);
		model.addAttribute("accounts", accounts);
		model.addAttribute("connection", connection);
		return "/content/account/social/association";
	}

	@RequestMapping(value = "/social/association", method = RequestMethod.POST)
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
	public String socialAssociationAction(@ModelAttribute("form") SocialAssociationForm form, Errors errors, Model model, NativeWebRequest request) {
		final ProviderSignInAttempt attempt = (ProviderSignInAttempt) request.getAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);
		if (attempt == null) {
			return "redirect:/account/social/finish";
		}

		final Connection<?> connection = attempt.getConnection();
		if (form.getUserId() != null) { // selection
			final Account account = accountManager.getAccount(form.getUserId());
			if (account != null) {
				return forwardToAuthorization(request, account, true, form.getFinish());
			} else {
				log.error("Very strange. No account after selection. Start again?");
				errors.reject("Inadmissible username");
			}
		} else {
			final UserProfile profile = connection.fetchUserProfile();

			final String email = profile.getEmail();
			final String username = profile.getName() == null ? profile.getUsername() : profile.getName();

			try {
				final Account account = accountManager.createAccount(email, UUID.randomUUID().toString(), new Passport(username, Language.RU, TimeZone.getTimeZone("GMT+00:00")));
				addAccountAssociation(account, connection);
				return forwardToAuthorization(request, account, true, form.getFinish());
			} catch (DuplicateAccountException e) {
				log.error("Very strange. DuplicateAccountException shouldn't be here.", e);
				errors.reject("Account with the same email already registered");
			} catch (InadmissibleUsernameException e) {
				log.error("Very strange. InadmissibleUsernameException is not what we suppose", e);
				errors.reject("Inadmissible username");
			}
		}
		return socialAssociation(form, model, request);
	}

	@RequestMapping("/social/finish")
	public String socialAssociationFinish(@RequestParam(value = "continue", defaultValue = "/privacy/view") String continueUrl, Model model) {
		model.addAttribute("continue", continueUrl);
		return "/content/account/social/finish";
	}

	@RequestMapping(value = "create", method = RequestMethod.POST)
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_UNCOMMITTED)
	public String createAccount(@ModelAttribute("login") AccountLoginForm login,
								@Valid @ModelAttribute("registration") AccountRegistrationForm form, BindingResult result,
								Model model, NativeWebRequest request, SessionStatus status, Locale locale) {
		log.info("Create new account request: {}", form);
		// Validate before next steps
		validateAccount(form, result, locale);

		Account account = null;
		if (!result.hasErrors()) {
			try {
				account = accountManager.createAccount(form.getEmail(), form.getPassword(), new Passport(form.getUsername()));
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
				result.reject("error.internal");
			}
		}

		if (result.hasErrors() || account == null) {
			log.info("Account form is not correct: {}", result);
			return signinInternal(login, result, form, model, request);
		} else {
			log.info("Account has been created.");

			status.setComplete();
			try {
				notificationService.raiseNotification(Recipient.get(account), Sender.ACCOUNTS, "account.created", account, account.getPassport().getUsername());
			} catch (NotificationException e) {
				log.error("Notification about new account can't be sent", e);
			}
			return forwardToAuthorization(request, account, form.isRememberMe(), null);
		}
	}

	protected static String forwardToAuthorization(final NativeWebRequest request, final Account account, final boolean rememberMe, final String continueUrl) {
		request.removeAttribute(ProviderSignInAttempt.SESSION_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);

		request.setAttribute("rememberMe", rememberMe, RequestAttributes.SCOPE_REQUEST);
		request.setAttribute("PRE_AUTHENTICATED_ACCOUNT", account, RequestAttributes.SCOPE_REQUEST);
		return "forward:/account/authorization" + (continueUrl != null ? "?continue=" + continueUrl : "");
	}

	private void addAccountAssociation(Account account, Connection<?> connection) {
		final ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(String.valueOf(account.getId()));
		connectionRepository.addConnection(connection);
	}

	private void validateAccount(AccountRegistrationForm form, Errors errors, Locale locale) {
		if (!form.getPassword().equals(form.getConfirm())) {
			errors.rejectValue("confirm", "account.register.pwd-cfr.err.mismatch");
		}
		checkAvailability(form.getEmail(), form.getUsername(), errors, locale);
	}

	@SuppressWarnings("deprecation")
	private void restoreAccountLoginForm(AccountLoginForm form, NativeWebRequest request) {
		if (form.getJ_username() == null) {
			final Authentication authentication;
			final AuthenticationException ex = (AuthenticationException) request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, RequestAttributes.SCOPE_SESSION);
			if (ex != null) {
				authentication = ex.getAuthentication();
			} else {
				authentication = SecurityContextHolder.getContext().getAuthentication();
			}

			if (authentication != null) {
				if (authentication.getPrincipal() instanceof MemberDetails) {
					final MemberDetails member = (MemberDetails) authentication.getPrincipal();
					form.setJ_username(member.getEmail());
				} else if (!(authentication instanceof AnonymousAuthenticationToken) && !(authentication.getPrincipal() instanceof Visitor)) {
					form.setJ_username(authentication.getName());
				}
			}
		}
	}

	@Autowired
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	@Autowired
	public void setServerDescriptor(final ServerDescriptor descriptor) {
		connectSupport = new ConnectSupport() {
			@Override
			protected String callbackUrl(NativeWebRequest request) {
				return descriptor.getWebHostName() + "/account/social/" + request.getParameter("provider");
			}
		};
		connectSupport.setUseAuthenticateUrl(true);
	}

	@Autowired
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Autowired
	public void setUsersConnectionRepository(UsersConnectionRepository usersConnectionRepository) {
		this.usersConnectionRepository = usersConnectionRepository;
	}

	@Autowired
	public void setAuthenticationServiceLocator(SocialAuthenticationServiceLocator authenticationServiceLocator) {
		this.authenticationServiceLocator = authenticationServiceLocator;
	}
}
