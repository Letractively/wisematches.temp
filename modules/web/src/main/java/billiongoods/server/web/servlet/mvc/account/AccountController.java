package billiongoods.server.web.servlet.mvc.account;

import billiongoods.core.Visitor;
import billiongoods.core.account.Account;
import billiongoods.core.account.AccountEditor;
import billiongoods.core.account.AccountManager;
import billiongoods.server.services.ServerDescriptor;
import billiongoods.server.services.notify.NotificationService;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.account.form.AccountLoginForm;
import billiongoods.server.web.servlet.mvc.account.form.AccountRegistrationForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ConnectSupport;
import org.springframework.social.connect.web.ProviderSignInAttempt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

import javax.inject.Inject;
import java.util.List;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/account")
public class AccountController extends AbstractController {
	private AccountManager accountManager;
	private NotificationService notificationService;

	private ConnectionFactoryLocator connectionFactoryLocator;
	private UsersConnectionRepository usersConnectionRepository;

	private final ConnectSupport webSupport = new ConnectSupport();

	private static final String SOCIAL_SIGNING_ATTEMPT = "SOCIAL_SIGNING_ATTEMPT";

	private static final Logger log = LoggerFactory.getLogger("billiongoods.web.mvc.AccountSocialController");

	@Inject
	public AccountController(ServerDescriptor descriptor) {
		super(true, false);

		webSupport.setApplicationUrl(descriptor.getWebHostName());
	}

	@RequestMapping("/signin")
	public String signinInternal(@ModelAttribute("login") AccountLoginForm login,
								 @ModelAttribute("registration") AccountRegistrationForm register,
								 BindingResult result, NativeWebRequest request) {
		restoreAccountLoginForm(login, request);

		final String error = login.getError();
		if (error != null) {
			switch (error) {
				case "credential":
					result.rejectValue("j_password", "account.signin.err.credential");
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
		return "/content/account/authorization";
	}

	@RequestMapping(value = "/social/association", method = RequestMethod.GET)
	public String socialAssociation(Model model, NativeWebRequest request) {
		final ProviderSignInAttempt attempt = (ProviderSignInAttempt) request.getAttribute(SOCIAL_SIGNING_ATTEMPT, RequestAttributes.SCOPE_SESSION);
		if (attempt == null) {
			return "redirect:/account/signin";
		}
		model.addAttribute("signInAttempt", attempt);
		return "/content/account/social/association";
	}

	@RequestMapping(value = "/social/association", method = RequestMethod.POST)
	public String socialAssociationAction(Model model, NativeWebRequest request) {
		final ProviderSignInAttempt attempt = (ProviderSignInAttempt) request.getAttribute(SOCIAL_SIGNING_ATTEMPT, RequestAttributes.SCOPE_SESSION);
		if (attempt == null) {
			return "redirect:/account/signin";
		}

		final AccountEditor editor = new AccountEditor();
		editor.setEmail("");
		editor.setUsername("");

//		accountManager.createAccount(new)

/*
		usersConnectionRepository.createConnectionRepository(userId).updateConnection(connection);
		attempt.getConnection()
		final ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
*/


		return "redirect:/"; // TODO: redirect to authorization
	}

	@RequestMapping(value = "/social/{providerId}", method = RequestMethod.POST)
	public String socialSignIn(@PathVariable String providerId, NativeWebRequest request) {
		final ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
		if (connectionFactory == null) {
			throw new ProviderNotFoundException(providerId);
		}
		return "redirect:" + webSupport.buildOAuthUrl(connectionFactory, request);
	}

	@RequestMapping(value = "/social/{providerId}", method = RequestMethod.GET)
	public String socialProcessing(@PathVariable String providerId, NativeWebRequest request) {
		final String code = request.getParameter("code");
		final String token = request.getParameter("oauth_token");

		Connection<?> connection = null;
		final ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(providerId);
		if (code != null) { // OAuth2
			connection = webSupport.completeConnection((OAuth2ConnectionFactory<?>) connectionFactory, request);
		} else if (token != null) { // OAuth1
			connection = webSupport.completeConnection((OAuth1ConnectionFactory<?>) connectionFactory, request);
		}

		if (connection != null) {
			List<String> userIds = usersConnectionRepository.findUserIdsWithConnection(connection);
			if (userIds.size() == 0) {
				final ProviderSignInAttempt signInAttempt = new ProviderSignInAttempt(connection, connectionFactoryLocator, usersConnectionRepository);
				request.setAttribute(SOCIAL_SIGNING_ATTEMPT, signInAttempt, RequestAttributes.SCOPE_SESSION);
				return "redirect:/account/social/association";
			} else if (userIds.size() == 1) {
				final String userId = userIds.get(0);
				final Account account = accountManager.getAccount(Long.getLong(userId));

				if (account != null) {
					usersConnectionRepository.createConnectionRepository(userId).updateConnection(connection);
				}
			} else {
				// TODO: TODO:Redirect here for authorization

//				return redirect(URIBuilder.fromUri(signInUrl).queryParam("error", "multiple_users").build().toString());
			}
		}
		return "redirect:/account/signin";
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
			if (authentication != null &&
					!(authentication instanceof AnonymousAuthenticationToken) &&
					!(authentication.getPrincipal() instanceof Visitor)) {
				form.setJ_username(authentication.getName());
			}
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

	@Autowired
	public void setConnectionFactoryLocator(ConnectionFactoryLocator connectionFactoryLocator) {
		this.connectionFactoryLocator = connectionFactoryLocator;
	}

	@Autowired
	public void setUsersConnectionRepository(UsersConnectionRepository usersConnectionRepository) {
		this.usersConnectionRepository = usersConnectionRepository;
	}
}
