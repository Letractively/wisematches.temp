package billiongoods.server.web.servlet.mvc.account;


import billiongoods.server.web.servlet.mvc.AbstractController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/account/modify")
@Deprecated
public class SettingsController extends AbstractController {
/*
	private AccountManager accountManager;
    private MemberDetailsService detailsService;
    private NotificationManager notificationManager;

    private static final Logger log = LoggerFactory.getLogger("billiongoods.web.mvc.SettingsController");

    public SettingsController() {
    }

    @RequestMapping(value = "")
    public String modifyAccountPage(Model model, @ModelAttribute("settings") SettingsForm form) {
        final Member member = getPrincipal(Member.class);
        final Account account = accountManager.getAccount(member.getId());
        if (account.getTimeZone() != null) {
            form.setTimezone(account.getTimeZone().getID());
        }
        form.setLanguage(account.getLanguage().name().toLowerCase());
        form.setEmail(account.getEmail());
        model.addAttribute("timeZones", TimeZoneInfo.getTimeZones());

        final Map<String, NotificationScope> descriptors = new HashMap<>();
        for (String code : new TreeSet<>(notificationManager.getNotificationCodes())) {
            descriptors.put(code, notificationManager.getNotificationScope(code, member));
        }
        model.addAttribute("notificationsView", new NotificationsTreeView(descriptors));
        return "/content/playground/settings/template";
    }

    @RequestMapping(value = "done")
    public String modifyAccountDone(Model model, @ModelAttribute("settings") SettingsForm form) {
        model.addAttribute("saved", Boolean.TRUE);
        return modifyAccountPage(model, form);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String modifyAccountAction(@Valid @ModelAttribute("settings") SettingsForm form,
                                      BindingResult errors, Model model, HttpServletRequest request) throws UnknownEntityException {
        final Member member = getPrincipal(Member.class);
        final Account account = accountManager.getAccount(member.getId());
        if (account == null) {
            throw new UnknownEntityException(null, "account");
        }

        final Set<String> codes = notificationManager.getNotificationCodes();
        for (String code : codes) {
            final String parameter = request.getParameter(code);
            final NotificationScope scope = parameter != null && !parameter.isEmpty() ? NotificationScope.valueOf(parameter.toUpperCase()) : null;
            notificationManager.setNotificationScope(code, member, scope);
        }

        Language language = account.getLanguage();
        if (form.getLanguage() != null) {
            try {
                language = Language.valueOf(form.getLanguage().toUpperCase());
            } catch (IllegalArgumentException ex) {
                errors.rejectValue("language", "account.register.language.err.unknown");
            }
        }

        TimeZone timeZone = account.getTimeZone();
        if (form.getTimezone() != null) {
            timeZone = TimeZone.getTimeZone(form.getTimezone());
            if (timeZone == null) {
                errors.rejectValue("timezone", "account.register.timezone.err.unknown");
            }
        }

        if (form.isChangeEmail() && form.getEmail().trim().isEmpty()) {
            errors.rejectValue("email", "account.register.email.err.blank");
        }

        if (form.isChangePassword()) {
            if (form.getPassword().trim().isEmpty()) {
                errors.rejectValue("password", "account.register.pwd.err.blank");
            }
            if (form.getConfirm().trim().isEmpty()) {
                errors.rejectValue("confirm", "account.register.pwd-cfr.err.blank");
            }

            if (!form.getPassword().equals(form.getConfirm())) {
                errors.rejectValue("confirm", "account.register.pwd-cfr.err.mismatch");
            }
        }

        if (!errors.hasErrors()) {
            boolean changeRequired = false;
            final AccountEditor editor = new AccountEditor(account);
            if (language != editor.getLanguage()) {
                changeRequired = true;
                editor.setLanguage(language);
            }
            if (!editor.getTimeZone().equals(timeZone)) {
                changeRequired = true;
                editor.setTimeZone(timeZone);
            }

            if (form.isChangeEmail() && !editor.getEmail().equals(form.getEmail())) {
                changeRequired = true;
                editor.setEmail(form.getEmail());
            }

            String pwd = null;
            if (form.isChangePassword()) {
                changeRequired = true;
                pwd = form.getPassword();
            }

            if (changeRequired) {
                try {
                    accountManager.updateAccount(editor.createAccount(), pwd);

                    final MemberDetails details = detailsService.loadMemberByEmail(account.getEmail());
                    if (details != null) {
                        SecurityContextHolder.getContext().setAuthentication(new MemberAuthenticationToken(details));
                    }
                    return "redirect:/account/modify#" + form.getOpenedTab();
                } catch (UnknownAccountException e) {
                    throw new UnknownEntityException(null, "account");
                } catch (DuplicateAccountException ex) {
                    final Set<String> fieldNames = ex.getFieldNames();
                    if (fieldNames.contains("email")) {
                        errors.rejectValue("email", "account.register.email.err.busy");
                    }
                    if (fieldNames.contains("nickname")) {
                        errors.rejectValue("nickname", "account.register.nickname.err.busy");
                    }
                } catch (InadmissibleUsernameException ex) {
                    errors.rejectValue("nickname", "account.register.nickname.err.incorrect");
                } catch (Exception ex) {
                    log.error("Account can't be created", ex);
                    errors.reject("billiongoods.error.internal");
                }
            }
        }
        return modifyAccountPage(model, form);
    }

    @Autowired
    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Autowired
    public void setDetailsService(MemberDetailsService detailsService) {
        this.detailsService = detailsService;
    }

    @Autowired
    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }
*/
}
