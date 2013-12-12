package billiongoods.server.web.servlet.mvc.account;

import billiongoods.core.Language;
import billiongoods.core.Member;
import billiongoods.core.Passport;
import billiongoods.core.account.Account;
import billiongoods.core.account.AccountAvailability;
import billiongoods.core.account.AccountManager;
import billiongoods.core.account.DuplicateAccountException;
import billiongoods.server.services.timezone.TimeZoneManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.Department;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import billiongoods.server.web.servlet.mvc.account.form.EmailForm;
import billiongoods.server.web.servlet.mvc.account.form.PassportForm;
import billiongoods.server.web.servlet.mvc.account.form.PasswordForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.SocialAuthenticationServiceLocator;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.TimeZone;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/account/passport")
public class PassportController extends AbstractController {
	private AccountManager accountManager;
	private TimeZoneManager timeZoneManager;

	private UsersConnectionRepository usersConnectionRepository;
	private SocialAuthenticationServiceLocator authenticationServiceLocator;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.web.mvc.SettingsController");

	public PassportController() throws IOException {
		super(false, true);
	}

	@Override
	@ModelAttribute("department")
	public Department getDepartment(HttpServletRequest request) {
		return Department.PRIVACY;
	}

	@RequestMapping(value = "/view")
	public String personalSettings(@ModelAttribute("form") PassportForm form, Model model) {
		final Member member = getMember();
		final Passport passport = member.getPassport();

		form.setUsername(passport.getUsername());
		form.setLanguage(passport.getLanguage().getCode());
		form.setTimeZone(passport.getTimeZone().getID());

		model.addAttribute("timeZones", timeZoneManager.getTimeZoneEntries(Language.RU));
		return "/content/account/passport/view";
	}

	@RequestMapping(value = "/personal", method = RequestMethod.GET)
	public String personalView(@ModelAttribute("form") PassportForm form, Model model) {
		personalSettings(form, model);
		return "/content/account/passport/personal";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/personal", method = RequestMethod.POST)
	public String personalAction(NativeWebRequest request, Model model, @ModelAttribute("form") PassportForm form, BindingResult result) {
		final Member principal = getMember();
		final Account account = accountManager.getAccount(principal.getId());
		if (account == null) {
			throw new UnknownEntityException(principal.getId(), "account");
		}

		try {
			final Language language = Language.byCode(form.getLanguage());
			final TimeZone timeZone = TimeZone.getTimeZone(form.getTimeZone());

			final Account acc = accountManager.updatePassport(account, new Passport(form.getUsername(), language, timeZone));
			return AccountController.forwardToAuthorization(request, acc, true, "/account/passport/view");
		} catch (DuplicateAccountException ex) {
			result.rejectValue("email", "account.register.email.err.busy");
		} catch (Exception ex) {
			log.error("Account can't be created", ex);
			result.reject("error.internal");
		}
		return personalView(form, model);
	}

	@RequestMapping(value = "/email", method = RequestMethod.GET)
	public String emailView(@ModelAttribute("form") EmailForm form) {
		final Member member = getMember();
		form.setEmail(member.getEmail());
		return "/content/account/passport/email";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/email", method = RequestMethod.POST)
	public String emailAction(NativeWebRequest request, @ModelAttribute("form") EmailForm form, BindingResult result) {
		final Member principal = getMember();
		final Account account = accountManager.getAccount(principal.getId());
		if (account == null) {
			throw new UnknownEntityException(principal.getId(), "account");
		}

		if (!account.getEmail().equalsIgnoreCase(form.getEmail())) {
			final AccountAvailability a = accountManager.validateAvailability(account.getPassport().getUsername(), form.getEmail());
			if (a.isAvailable()) {
				try {
					final Account acc = accountManager.updateEmail(account, form.getEmail());
					return AccountController.forwardToAuthorization(request, acc, true, "/account/passport/view");
				} catch (DuplicateAccountException ex) {
					result.rejectValue("email", "account.register.email.err.busy");
				} catch (Exception ex) {
					log.error("Account can't be created", ex);
					result.reject("error.internal");
				}
			} else {
				if (result != null && !a.isEmailAvailable()) {
					result.rejectValue("email", "account.register.email.err.busy");
				}
				if (result != null && !a.isUsernameProhibited()) {
					result.rejectValue("nickname", "account.register.nickname.err.incorrect");
				}
			}
		}
		return emailView(form);
	}

	@RequestMapping(value = "/password", method = RequestMethod.GET)
	public String passwordView(@ModelAttribute("form") PasswordForm form) {
		return "/content/account/passport/password";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/password", method = RequestMethod.POST)
	public String passwordAction(NativeWebRequest request, @ModelAttribute("form") PasswordForm form, BindingResult result) {
		final Member principal = getMember();
		final Account account = accountManager.getAccount(principal.getId());
		if (account == null) {
			throw new UnknownEntityException(principal.getId(), "account");
		}

		if (form.getPassword().equals(form.getConfirm())) {
			try {
				final Account acc = accountManager.updatePassword(account, form.getPassword());
				return AccountController.forwardToAuthorization(request, acc, true, "/account/passport/view");
			} catch (Exception ex) {
				log.error("Account can't be created", ex);
				result.reject("error.internal");
			}
		} else {
			result.rejectValue("confirm", "account.register.pwd-cfr.err.mismatch");
		}
		return passwordView(form);
	}

	@RequestMapping(value = "/social", method = RequestMethod.GET)
	public String socialView(Model model) {
		final String userId = String.valueOf(getMember().getId());
		final ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(userId);
		final MultiValueMap<String, Connection<?>> allConnections = connectionRepository.findAllConnections();

		model.addAttribute("connections", allConnections);
		model.addAttribute("socialProviders", authenticationServiceLocator.registeredAuthenticationProviderIds());

		return "/content/account/passport/social";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/social", method = RequestMethod.POST)
	public String socialAction(@RequestParam("connectionKey") String action, Model model) {
		final int index = action.indexOf('|');

		final String userId = String.valueOf(getMember().getId());
		final ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(userId);
		connectionRepository.removeConnection(new ConnectionKey(action.substring(0, index), action.substring(index + 1)));

		return socialView(model);
	}

	@Autowired
	public void setAccountManager(AccountManager accountManager) {
		this.accountManager = accountManager;
	}

	@Autowired
	public void setTimeZoneManager(TimeZoneManager timeZoneManager) {
		this.timeZoneManager = timeZoneManager;
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
