package billiongoods.server.services.notify.impl;


import billiongoods.core.personality.player.account.Account;
import billiongoods.core.personality.player.account.AccountListener;
import billiongoods.core.personality.player.account.AccountManager;
import billiongoods.server.services.ServerDescriptor;
import billiongoods.server.services.notify.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.Date;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class AlertsOriginCenter {
    private MailSender mailSender;
    private AccountManager accountManager;
    private ServerDescriptor serverDescriptor;

    private final TheAccountListener accountListener = new TheAccountListener();

    private static final Logger log = LoggerFactory.getLogger("billiongoods.alerts.OriginCenter");

    public AlertsOriginCenter() {
    }

    protected void raiseAlarm(String system, String subj, String msg) {
        try {
            final SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(serverDescriptor.getAlertsMailBox());
            message.setFrom(NotificationSender.SUPPORT.getMailAddress(serverDescriptor));
            message.setSentDate(new Date());
            message.setSubject("[" + system + "] " + subj);
            message.setText(msg);

            mailSender.send(message);
        } catch (Exception ex) {
            log.error("Alerts can't be sent: system=[{}], subj=[{}], msg=[{}]", system, subj, msg);
        }
    }

    public void setServerDescriptor(ServerDescriptor serverDescriptor) {
        this.serverDescriptor = serverDescriptor;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setAccountManager(AccountManager accountManager) {
        if (this.accountManager != null) {
            this.accountManager.removeAccountListener(accountListener);
        }

        this.accountManager = accountManager;

        if (this.accountManager != null) {
            this.accountManager.addAccountListener(accountListener);
        }
    }

    private class TheAccountListener implements AccountListener {
        private TheAccountListener() {
        }

        @Override
        public void accountCreated(Account account) {
            raiseAlarm("ACC", "Account created: " + account.getNickname(), account.getNickname() + " (" + account.getEmail() + ")");
        }

        @Override
        public void accountRemove(Account account) {
        }

        @Override
        public void accountUpdated(Account oldAccount, Account newAccount) {
        }
    }
}
