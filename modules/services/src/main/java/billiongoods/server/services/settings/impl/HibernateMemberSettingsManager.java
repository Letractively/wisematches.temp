package billiongoods.server.services.settings.impl;

import billiongoods.core.Language;
import billiongoods.core.account.Account;
import billiongoods.server.services.settings.MemberSettings;
import billiongoods.server.services.settings.MemberSettingsManager;
import billiongoods.server.services.settings.NotificationGate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.TimeZone;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
public class HibernateMemberSettingsManager implements MemberSettingsManager {
	private SessionFactory sessionFactory;

	private static final MemberSettings MEMBER_SETTINGS = new MemberSettings(Language.RU, TimeZone.getDefault(), NotificationGate.PAYMENT_EMAIL);

	public HibernateMemberSettingsManager() {
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public MemberSettings getMemberSettings(Account account) {
		final Session session = sessionFactory.getCurrentSession();

		final Object o = session.get(HibernateMemberSettings.class, account.getId());
		if (o == null) {
			return MEMBER_SETTINGS;
		}
		return (HibernateMemberSettings) o;
	}

	@Override
	@Transactional(propagation = Propagation.MANDATORY)
	public void setMemberSettings(Account account, MemberSettings settings) {
		final Session session = sessionFactory.getCurrentSession();

		HibernateMemberSettings hs = (HibernateMemberSettings) session.get(HibernateMemberSettings.class, account.getId());
		if (hs == null) {
			hs = new HibernateMemberSettings(account.getId(), settings);
			session.save(hs);
		} else {
			hs.update(settings);
			session.update(hs);
		}
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
