package billiongoods.server.web.servlet.mvc.assistance;

import billiongoods.core.Passport;
import billiongoods.server.services.notify.NotificationService;
import billiongoods.server.services.notify.Recipient;
import billiongoods.server.services.notify.Sender;
import billiongoods.server.web.servlet.mvc.AbstractController;
import billiongoods.server.web.servlet.mvc.UnknownEntityException;
import billiongoods.server.web.servlet.mvc.assistance.impl.IssueForm;
import billiongoods.server.web.servlet.sdo.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;
import java.util.UUID;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Controller
@RequestMapping("/assistance")
public class AssistanceController extends AbstractController {
	private NotificationService notificationService;

	private static final Logger log = LoggerFactory.getLogger("billiongoods.assistance.AssistanceController");

	public AssistanceController() {
	}

	@RequestMapping({"", "/"})
	public String helpCenterPage(Model model) {
		return "/content/assistance/general";
	}

	@RequestMapping("/{pageName}")
	public String infoPages(@PathVariable String pageName, Model model, Locale locale) throws UnknownEntityException {
		if (!staticContentGenerator.generatePage(pageName, false, model, locale)) {
			throw new UnknownEntityException(pageName, "assistance");
		}
		return "/content/assistance/layout";
	}

	@RequestMapping("/tip.ajax")
	public ServiceResponse loadTip(@RequestParam("s") String section, Locale locale) {
		return responseFactory.success(messageSource.getMessage("game.tip." + section, locale));
	}

	@RequestMapping("/question.ajax")
	public ServiceResponse reportIssue(@RequestBody final IssueForm form, Locale locale) {
		if (form.getEmail() == null || form.getEmail().isEmpty()) {
			return responseFactory.failure("assistance.question.email.empty", locale);
		}
		if (form.getMessage() == null || form.getMessage().isEmpty()) {
			return responseFactory.failure("assistance.question.message.empty", locale);
		}

		log.info("New question received: {}", form);

		final String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

		try {
			final Recipient returnAddress = Recipient.get(form.getEmail(), new Passport(form.getName()));
			final Recipient recipient = Recipient.get(Recipient.MailBox.SUPPORT, returnAddress);

			notificationService.raiseNotification(recipient, Sender.SERVER, "system.support", form, id);
		} catch (Exception ex) {
			log.error("Question notification can't be sent", ex);
			return responseFactory.failure("assistance.question.system", locale);
		}
		return responseFactory.success(id);
	}

	@Autowired
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}
}
