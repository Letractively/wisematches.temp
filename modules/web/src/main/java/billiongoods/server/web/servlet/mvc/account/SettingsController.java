package billiongoods.server.web.servlet.mvc.account;

import billiongoods.core.Language;
import billiongoods.core.Member;
import billiongoods.core.Personality;
import billiongoods.core.account.AccountManager;
import billiongoods.server.services.settings.MemberSettings;
import billiongoods.server.services.settings.MemberSettingsManager;
import billiongoods.server.services.settings.NotificationGate;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.TimeZone;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/account/settings")
public class SettingsController extends AbstractController {
	private AccountManager accountManager;
	private TimeZoneManager timeZoneManager;
	private MemberSettingsManager settingsManager;

	private UsersConnectionRepository usersConnectionRepository;

	private static final Logger log = LoggerFactory.getLogger("wisematches.web.mvc.SettingsController");

	public SettingsController() throws IOException {
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
		final TimeZone timeZone = principal.getSettings().getTimeZone();

		form.setUsername(principal.getUsername());
		form.setTimeZone(timeZone.getID());

		model.addAttribute("timeZones", timeZoneManager.getTimeZoneEntries(Language.RU));
		return "/content/account/settings/personal";
	}

	@RequestMapping(value = "/modify")
	public String modifySettings(@ModelAttribute("form") SettingsForm form, Model model) {
		personalSettings(form, model);

		return "/content/account/settings/modify";
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	public String modifySettingsAction(@ModelAttribute("form") SettingsForm form, Model model) {
		final Member principal = (Member) getPrincipal();

		settingsManager.setMemberSettings(accountManager.getAccount(principal.getId()),
				new MemberSettings(Language.RU, TimeZone.getTimeZone(form.getTimeZone()), NotificationGate.PRIMARY_EMAIL));

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

		return "/content/account/settings/social";
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
	public void setSettingsManager(MemberSettingsManager settingsManager) {
		this.settingsManager = settingsManager;
	}

	@Autowired
	public void setUsersConnectionRepository(UsersConnectionRepository usersConnectionRepository) {
		this.usersConnectionRepository = usersConnectionRepository;
	}
}
