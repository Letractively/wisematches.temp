package billiongoods.server.services.notify.impl.publisher;


import billiongoods.core.Language;
import billiongoods.server.services.ServerDescriptor;
import billiongoods.server.services.notify.Notification;
import billiongoods.server.services.notify.NotificationScope;
import billiongoods.server.services.notify.NotificationSender;
import billiongoods.server.services.notify.PublicationException;
import billiongoods.server.services.notify.impl.NotificationPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MailNotificationPublisher implements NotificationPublisher {
	private JavaMailSender mailSender;
	private MessageSource messageSource;
	private ServerDescriptor serverDescriptor;

	private final Map<SenderKey, InternetAddress> addressesCache = new HashMap<>();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.notification.MailPublisher");

	public MailNotificationPublisher() {
	}

	@Override
	public String getName() {
		return "email";
	}

	@Override
	public NotificationScope getNotificationScope() {
		return NotificationScope.EXTERNAL;
	}

	@Override
	public void publishNotification(final Notification notification) throws PublicationException {
		throw new UnsupportedOperationException("TODO: commented");

/*
		log.debug("Send mail notification '{}' to {}", notification.getCode(), notification.getTarget());
        final MimeMessagePreparator mm = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                final Member member = notification.getTarget();
                final Language language = member.getLanguage();

                final InternetAddress to = new InternetAddress(member.getEmail(), member.getUsername(), "UTF-8");
                final InternetAddress from = getInternetAddress(notification.getSender(), language);

                final MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, false, "UTF-8");
                msg.setFrom(from);
                msg.setTo(to);
                msg.setSubject(notification.getSubject());

                final StringBuilder m = new StringBuilder();
                final Locale locale = notification.getTarget().getLanguage().getLocale();
                m.append(messageSource.getMessage("notify.mail.header", null, locale));
                m.append(" <b>").append(notification.getTarget().getUsername()).append("</b>.");
                m.append(notification.getMessage());
                m.append("<p><hr><br>");
                m.append(messageSource.getMessage("notify.mail.footer", null, locale));
                m.append("</p>");
                msg.setText(m.toString(), true);
            }
        };
        try {
            mailSender.send(mm);
        } catch (MailException ex) {
            throw new PublicationException(ex);
        }
*/
	}

	protected InternetAddress getInternetAddress(NotificationSender sender, Language language) {
		return addressesCache.get(new SenderKey(sender, language));
	}

	private void validateAddressesCache() {
		addressesCache.clear();

		if (messageSource == null || serverDescriptor == null) {
			return;
		}

		for (NotificationSender sender : NotificationSender.values()) {
			for (Language language : Language.values()) {
				try {
					final String address = messageSource.getMessage("mail.address." + sender.getUserInfo(),
							null, sender.getMailAddress(serverDescriptor), language.getLocale());

					final String personal = messageSource.getMessage("mail.personal." + sender.getUserInfo(),
							null, sender.name(), language.getLocale());

					addressesCache.put(new SenderKey(sender, language), new InternetAddress(address, personal, "UTF-8"));
				} catch (UnsupportedEncodingException ex) {
					log.error("JAVA SYSTEM ERROR - NOT UTF8!", ex);
				}
			}
		}
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
		validateAddressesCache();
	}

	public void setServerDescriptor(ServerDescriptor serverDescriptor) {
		this.serverDescriptor = serverDescriptor;
		validateAddressesCache();
	}

	private static final class SenderKey {
		private final Language language;
		private final NotificationSender sender;

		private SenderKey(NotificationSender sender, Language language) {
			this.sender = sender;
			this.language = language;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			SenderKey senderKey = (SenderKey) o;
			return language == senderKey.language && sender == senderKey.sender;
		}

		@Override
		public int hashCode() {
			int result = language.hashCode();
			result = 31 * result + sender.hashCode();
			return result;
		}
	}
}