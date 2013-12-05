package billiongoods.server.services.settings.impl;

import billiongoods.server.services.settings.MemberSettings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Sergey Klimenko (smklimenko@gmail.com)
 */
@Entity
@Table(name = "account_settings")
public class HibernateMemberSettings extends MemberSettings {
	@Id
	@Column(name = "id", insertable = true, updatable = false)
	private Long id;

	@Deprecated
	HibernateMemberSettings() {
	}

	public HibernateMemberSettings(Long id, MemberSettings settings) {
		this.id = id;
		update(settings);
	}

	void update(MemberSettings settings) {
		this.language = settings.getLanguage();
		this.timeZone = settings.getTimeZone();
		this.ordersGate = settings.getOrdersGate();
	}
}
