package billiongoods.server.web.servlet.mvc.account;

import billiongoods.core.Language;
import billiongoods.core.Member;
import billiongoods.core.Passport;
import billiongoods.core.Personality;
import billiongoods.core.account.Account;
import billiongoods.core.account.AccountManager;
import billiongoods.core.account.DuplicateAccountException;
import billiongoods.core.account.InadmissibleUsernameException;
import billiongoods.server.services.timezone.TimeZoneManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.Department;
import billiongoods.server.web.servlet.mvc.account.form.SettingsForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;
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

	private static final Logger log = LoggerFactory.getLogger("wisematches.web.mvc.SettingsController");

	public PassportController() throws IOException {
		super(false, true);
	}

	@Override
	@ModelAttribute("department")
	public Department getDepartment(HttpServletRequest request) {
		return Department.PRIVACY;
	}

	@RequestMapping(value = "/personal")
	public String personalSettings(@ModelAttribute("form") SettingsForm form, Model model) {
		final Member principal = (Member) getPrincipal();

		final Passport passport = principal.getPassport();

		form.setUsername(passport.getUsername());
		form.setTimeZone(passport.getTimeZone().getID());
		form.setLanguage(passport.getLanguage().getCode());

		model.addAttribute("timeZones", timeZoneManager.getTimeZoneEntries(Language.RU));
		return "/content/account/passport/personal";
	}

	@RequestMapping(value = "/modify")
	public String modifySettings(@ModelAttribute("form") SettingsForm form, Model model) {
		personalSettings(form, model);

		return "/content/account/passport/modify";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	public String modifySettingsAction(NativeWebRequest request, Model model, @ModelAttribute("form") SettingsForm form, BindingResult result) {
		final Member principal = (Member) getPrincipal();

		final Account account = accountManager.getAccount(principal.getId());
		if (account != null) {
			try {
				final Language language = Language.byCode(form.getLanguage());
				final TimeZone timeZone = TimeZone.getTimeZone(form.getTimeZone());

				final Account acc = accountManager.updatePassport(account, new Passport(form.getUsername(), language, timeZone));
				return AccountController.forwardToAuthorization(request, acc, true, "/content/account/passport/modify");
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
		return modifySettings(form, model);
	}

	@RequestMapping(value = "/social")
	public String socialSettings(Model model) {
		final Personality principal = getPrincipal();

		final String userId = String.valueOf(principal.getId());
		final ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(userId);

		final MultiValueMap<String, Connection<?>> allConnections = connectionRepository.findAllConnections();

		model.addAttribute("providers", allConnections.keySet());
		model.addAttribute("connections", allConnections);

		return "/content/account/passport/social";
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
}
