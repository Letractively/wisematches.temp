package billiongoods.server.services.notify.impl.publisher;


import billiongoods.core.Language;
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

    private final Map<SenderKey, InternetAddress> addressesCache = new HashMap<>();

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
                final Recipient recipient = notification.getRecipient();

                String email = recipient.getEmail();
                String username = recipient.getUsername();

                if (recipient == Recipient.ALERTS_BOX) {
                    email = serverDescriptor.getAlertsMailBox();
                }
                final InternetAddress to = new InternetAddress(email, username, "UTF-8");
                final InternetAddress from = getInternetAddress(notification.getSender(), language);

                final MimeMessageHelper msg = new MimeMessageHelper(mimeMessage, false, "UTF-8");
                msg.setFrom(from);
                msg.setTo(to);
                msg.setSubject(notification.getSubject());

                final StringBuilder m = new StringBuilder();
                final Locale locale = language.getLocale();
                m.append(messageSource.getMessage("notify.mail.header", null, locale));
                if (recipient.getUsername() != null) {
                    m.append(" <b>").append(recipient.getUsername()).append("</b>.");
                } else {
                    m.append(" <b>").append(messageSource.getMessage("notify.mail.customer", null, locale)).append("</b>.");
                }
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
    }

    protected InternetAddress getInternetAddress(Sender sender, Language language) {
        return addressesCache.get(new SenderKey(sender, language));
    }

    private void validateAddressesCache() {
        addressesCache.clear();

        if (messageSource == null || serverDescriptor == null) {
            return;
        }

        for (Sender sender : Sender.values()) {
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
}