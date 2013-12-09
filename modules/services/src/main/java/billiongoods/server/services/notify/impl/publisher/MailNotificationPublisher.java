package billiongoods.server.services.notify.impl.publisher;


import billiongoods.core.Language;
import billiongoods.core.Passport;
import billiongoods.server.services.ServerDescriptor;
import billiongoods.server.services.notify.Notification;
import billiongoods.server.services.notify.PublicationException;
import billiongoods.server.services.notify.Recipient;
import billiongoods.server.services.notify.Sender;
import billiongoods.server.services.notify.impl.NotificationPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class MailNotificationPublisher implements NotificationPublisher {
	private JavaMailSender mailSender;
	private MessageSource messageSource;
	private ServerDescriptor serverDescriptor;

	private final Map<SenderKey, InternetAddress> senderCache = new HashMap<>();
	private final Map<RecipientKey, InternetAddress> recipientCache = new HashMap<>();

	private static final Logger log = LoggerFactory.getLogger("billiongoods.notification.MailPublisher");

	public MailNotificationPublisher() {
	}

	@Override
	public String getName() {
		return "email";
	}

	@Override
	public void publishNotification(final Notification notification) throws PublicationException {
		log.debug("Send mail notification '{}' to {}", notification.getCode(), notification.getRecipient());
		final MimeMessagePreparator mm = new MimeMessagePreparator() {
			public void prepare(MimeMessage mimeMessage) throws Exception {
				final Language language = Language.RU;
				final MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, false, "UTF-8");

				msg.setSubject(notification.getSubject());
				msg.setFrom(getInternetAddress(notification.getSender(), language));
				msg.setTo(getInternetAddress(notification.getRecipient(), language));

				final Recipient recipient = notification.getRecipient();
				if (recipient instanceof Recipient.Application) {
					final Recipient.Application application = (Recipient.Application) recipient;
					if (application.getReturnAddress() != null) {
						msg.setReplyTo(getInternetAddress(application.getReturnAddress(), language));
					}
					msg.setText(notification.getMessage(), true);
				} else if (recipient instanceof Recipient.Person) {
					final Recipient.Person person = (Recipient.Person) recipient;

					final StringBuilder m = new StringBuilder();
					final Locale locale = language.getLocale();
					m.append(messageSource.getMessage("notify.mail.header", null, locale));

					final Passport passport = person.getPassport();
					if (passport != null) {
						m.append(" <b>").append(passport.getUsername()).append("</b>.");
					} else {
						m.append(" <b>").append(messageSource.getMessage("notify.mail.customer", null, locale)).append("</b>.");
					}

					m.append(notification.getMessage());

					m.append("<p><hr><br>");
					m.append(messageSource.getMessage("notify.mail.footer", null, locale));
					m.append("</p>");
					msg.setText(m.toString(), true);
				} else {
					log.error("There is not processor for recipient {}", recipient);
				}
			}
		};
		try {
			mailSender.send(mm);
		} catch (MailException ex) {
			throw new PublicationException(ex);
		}
	}

	protected InternetAddress getInternetAddress(Recipient recipient, Language language) throws UnsupportedEncodingException {
		if (recipient instanceof Recipient.Person) {
			final Recipient.Person person = (Recipient.Person) recipient;
			final Passport passport = person.getPassport();
			return new InternetAddress(person.getEmail(), passport != null ? passport.getUsername() : "", "UTF-8");
		} else if (recipient instanceof Recipient.Application) {
			final Recipient.Application application = (Recipient.Application) recipient;
			return recipientCache.get(new RecipientKey(application.getMailBox(), language));
		}
		throw new IllegalArgumentException("Unsupported recipient: " + recipient);
	}

	protected InternetAddress getInternetAddress(Sender sender, Language language) {
		return senderCache.get(new SenderKey(sender, language));
	}

	private void validateAddressesCache() {
		senderCache.clear();

		if (messageSource == null || serverDescriptor == null) {
			return;
		}

		for (Language language : Language.values()) {
			for (Sender sender : Sender.values()) {
				try {
					final String address = messageSource.getMessage("mail.address." + sender.getCode(),
							null, sender.getCode() + "@" + serverDescriptor.getMailHostName(), language.getLocale());

					final String personal = messageSource.getMessage("mail.personal." + sender.getCode(),
							null, sender.name(), language.getLocale());

					senderCache.put(new SenderKey(sender, language), new InternetAddress(address, personal, "UTF-8"));
				} catch (UnsupportedEncodingException ex) {
					log.error("JAVA SYSTEM ERROR - NOT UTF8!", ex);
				}
			}

			for (Recipient.MailBox mailBox : Recipient.MailBox.values()) {
				try {
					final String address = messageSource.getMessage("mail.address." + mailBox.getCode(),
							null, mailBox.getCode() + "@" + serverDescriptor.getMailHostName(), language.getLocale());

					final String personal = messageSource.getMessage("mail.personal." + mailBox.getCode(),
							null, mailBox.name(), language.getLocale());

					recipientCache.put(new RecipientKey(mailBox, language), new InternetAddress(address, personal, "UTF-8"));
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
		private final Sender sender;

		private SenderKey(Sender sender, Language language) {
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

	private static final class RecipientKey {
		private final Language language;
		private final Recipient.MailBox mailBox;

		private RecipientKey(Recipient.MailBox mailBox, Language language) {
			this.language = language;
			this.mailBox = mailBox;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof RecipientKey)) return false;

			RecipientKey that = (RecipientKey) o;
			return language == that.language && mailBox == that.mailBox;
		}

		@Override
		public int hashCode() {
			int result = language.hashCode();
			result = 31 * result + mailBox.hashCode();
			return result;
		}
	}
}