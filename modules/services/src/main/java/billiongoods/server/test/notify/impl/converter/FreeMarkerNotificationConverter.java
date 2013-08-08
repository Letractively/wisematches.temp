package billiongoods.server.test.notify.impl.converter;

import billiongoods.core.Member;
import billiongoods.server.test.notify.Notification;
import billiongoods.server.test.notify.NotificationSender;
import billiongoods.server.test.notify.TransformationException;
import billiongoods.server.test.notify.impl.NotificationConverter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.context.MessageSource;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class FreeMarkerNotificationConverter implements NotificationConverter {
	private MessageSource messageSource;
	private Configuration freeMarkerConfig;

	private static final AtomicLong ID_GENERATOR = new AtomicLong();

	public FreeMarkerNotificationConverter() {
	}

	@Override
	public Notification createNotification(String code, Member target, NotificationSender sender, Object context) throws TransformationException {
		final Locale locale = target.getLanguage().getLocale();

		final String subject = messageSource.getMessage("notify.subject." + code, null, locale);

		final Map<String, Object> variables = new HashMap<>();
		// info
		variables.put("code", code);
		variables.put("template", getTemplate(code));
		// people
		variables.put("sender", sender);
		variables.put("personality", target);
		// common
		variables.put("locale", locale);
		variables.put("context", context);

		try {
			final Template ft = freeMarkerConfig.getTemplate("notification.ftl", locale, "UTF-8");
			final String message = FreeMarkerTemplateUtils.processTemplateIntoString(ft, variables);
			return new Notification(ID_GENERATOR.incrementAndGet(), code, subject, message, target, sender);
		} catch (IOException | TemplateException ex) {
			throw new TransformationException(ex);
		}
	}

	protected String getTemplate(String code) {
		int count = 0;
		int index = 0;
		do {
			index = code.indexOf('.', index + 1);
			if (index == -1) {
				break;
			}
			count++;
			if (count == 3) {
				return code.substring(0, index);
			}
		} while (true);
		return code;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setFreeMarkerConfig(Configuration freeMarkerConfig) {
		this.freeMarkerConfig = freeMarkerConfig;
	}
}
