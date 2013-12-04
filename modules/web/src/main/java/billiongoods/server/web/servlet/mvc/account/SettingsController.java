package billiongoods.server.web.servlet.mvc.account;

import billiongoods.core.Member;
import billiongoods.core.Personality;
import billiongoods.core.account.AccountManager;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.Department;
import billiongoods.server.web.servlet.mvc.account.form.SettingsForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.SocialAuthenticationServiceLocator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/account/settings")
public class SettingsController extends AbstractController {
	private AccountManager accountManager;

	private UsersConnectionRepository usersConnectionRepository;
	private SocialAuthenticationServiceLocator socialAuthenticationServiceLocator;

	private static final Logger log = LoggerFactory.getLogger("wisematches.web.mvc.SettingsController");

	public SettingsController() {
		super(false, true);
	}

	@Override
	@ModelAttribute("department")
	public Department getDepartment(HttpServletRequest request) {
		return Department.PRIVACY;
	}

	@RequestMapping(value = "/personal")
	public String personalSettings(Model model, @ModelAttribute("settings") SettingsForm form) {
		final Member principal = (Member) getPrincipal();

		form.setEmail(principal.getEmail());

		return "/content/account/settings/personal";
	}

	@RequestMapping(value = "/notifications")
	public String notificationSettings(Model model) {
		return "/content/account/settings/notifications";
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
	public void setUsersConnectionRepository(UsersConnectionRepository usersConnectionRepository) {
		this.usersConnectionRepository = usersConnectionRepository;
	}

	@Autowired
	public void setSocialAuthenticationServiceLocator(SocialAuthenticationServiceLocator socialAuthenticationServiceLocator) {
		this.socialAuthenticationServiceLocator = socialAuthenticationServiceLocator;
	}
}
